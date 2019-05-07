package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.brilliant.academe.domain.common.CommonResponse;
import com.brilliant.academe.domain.course.CourseCategory;
import com.brilliant.academe.domain.course.CreateCourseRequest;
import com.brilliant.academe.util.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.model.Sku;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.brilliant.academe.constant.Constant.*;

public class CreateCourseHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDB dynamoDB;
    private String courseId;
    private Item configItem;
    private String[] attributes = {"cfDistributionName", "stripeSecretKey"};

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        initDynamoDbClient();
        initConfig();
        return execute(requestEvent);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    private void initConfig(){
        configItem = CommonUtils.getConfigInfo(dynamoDB, attributes);
    }

    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent requestEvent){
        String token = requestEvent.getHeaders().get("Authorization");
        String userId = CommonUtils.getUserFromToken(token);
        CommonResponse response = new CommonResponse();
        response.setMessage(STATUS_FAILED);

        Item instructorItem = dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER).getItem("id", userId);
        if(Objects.nonNull(instructorItem) && Objects.nonNull(instructorItem.get("instructor"))){
            boolean isInstructor = (Boolean) instructorItem.get("instructor");
            if(isInstructor){
                ObjectMapper objectMapper = new ObjectMapper();
                CreateCourseRequest createCourseRequest = null;
                try {
                    createCourseRequest = objectMapper.readValue(requestEvent.getBody(), CreateCourseRequest.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                createCourseRequest.setInstructorId(userId);
                createCourseRequest.setInstructorName((String) instructorItem.get("fullName"));
                String cfDistributionName = (String) configItem.get("cfDistributionName");
                persistData(createCourseRequest, cfDistributionName);
                response.setMessage(STATUS_SUCCESS);
            }
        }
        return constructResponse(response, courseId);
    }

    private APIGatewayProxyResponseEvent constructResponse(CommonResponse response, String courseId){
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(new Gson().toJson(response));
        if(response.getMessage().equals(STATUS_SUCCESS)){
            Map<String, String> headers = new HashMap<>();
            headers.put("id", courseId);
            responseEvent.setHeaders(headers);
        }
        return responseEvent;
    }

    private void persistData(CreateCourseRequest createCourseRequest, String cfDistributionName)
            throws ConditionalCheckFailedException {
        System.out.println("cfDistName:"+ cfDistributionName);
        String FORMATTED_COURSE_NAME = createCourseRequest.getCourseName().replaceAll(" ", "+");
        String CF_IMAGE_URL = "https://" + cfDistributionName + "/" + CF_IMAGES_ORIGIN_PATH +"/";
        if(createCourseRequest.getSections() != null && createCourseRequest.getSections().size() > 0){
            createCourseRequest.getSections().forEach(section -> {
                if(section.getLectures() != null && section.getLectures().size() > 0){
                    section.getLectures().forEach(lecture->{
                        lecture.setLectureId(UUID.randomUUID().toString());
                        lecture.setLectureLink(CF_VIDEOS_ORIGIN_PATH + "/" + FORMATTED_COURSE_NAME+"/"+lecture.getLectureLink());
                        if(lecture.getMaterials()!= null && lecture.getMaterials().size() > 0) {
                            lecture.getMaterials().forEach(material -> {
                                material.setMaterialId(UUID.randomUUID().toString());
                                material.setMaterialLink(CF_VIDEOS_ORIGIN_PATH + "/" + FORMATTED_COURSE_NAME + "/" + material.getMaterialLink());
                            });
                        }
                    });
                }
            });
        }

        createCourseRequest.getSections().forEach(cs->{
            cs.getLectures().forEach(lecture->{
                lecture.setLectureId(UUID.randomUUID().toString());
            });
        });


        String sectionDetails = "NA";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            sectionDetails = objectMapper.writeValueAsString(createCourseRequest.getSections());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Table courseTable = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE);
        courseId = UUID.randomUUID().toString();
        for(CourseCategory courseCategory: createCourseRequest.getCourseCategories()){
            courseTable.putItem(new PutItemSpec().withItem(new Item()
                    .withString("id", courseId)
                    .withString("courseName", createCourseRequest.getCourseName())
                    .withString("description", createCourseRequest.getCourseDescription())
                    .withString("coverImage", CF_IMAGE_URL + FORMATTED_COURSE_NAME + "/" + createCourseRequest.getCoverImage())
                    .withString("courseLevel", createCourseRequest.getCourseLevel())
                    .withDouble("price", createCourseRequest.getCoursePrice().doubleValue())
                    .withDouble("discountedPrice", createCourseRequest.getDiscountedCoursePrice().doubleValue())
                    .withString("instructorId", createCourseRequest.getInstructorId())
                    .withString("instructorName", createCourseRequest.getInstructorName())
                    .withString("categoryId", courseCategory.getCourseCategoryId())
                    .withString("categoryName", courseCategory.getCourseCategoryName())
                    .withString("categoryDescription", courseCategory.getCourseCategoryDescription())
                    .withNumber("courseDuration", createCourseRequest.getCourseDuration())
                    .withString("courseType", createCourseRequest.getCourseType())));
        }

        String skuId = "NA";
        if(!createCourseRequest.getDiscountedCoursePrice().equals(new BigDecimal(0))) {
            skuId = createProductAndSkuInStripe(createCourseRequest.getDiscountedCoursePrice(),
                    createCourseRequest.getCourseName(), createCourseRequest.getCoverImage(), FORMATTED_COURSE_NAME, CF_IMAGE_URL);
        }

        dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE)
                .putItem(new PutItemSpec().withItem(new Item()
                    .withString("id", courseId)
                    .withDouble("price", createCourseRequest.getCoursePrice().doubleValue())
                    .withDouble("discountedPrice", createCourseRequest.getDiscountedCoursePrice().doubleValue())
                    .withString("courseName", createCourseRequest.getCourseName())
                    .withString("description", createCourseRequest.getCourseDescription())
                    .withString("coverImage",CF_IMAGE_URL + FORMATTED_COURSE_NAME + "/" + createCourseRequest.getCoverImage())
                    .withString("courseLevel", createCourseRequest.getCourseLevel())
                    .withString("instructorId", createCourseRequest.getInstructorId())
                    .withString("instructorName", createCourseRequest.getInstructorName())
                    .withNumber("courseDuration", createCourseRequest.getCourseDuration())
                    .withString("courseType", createCourseRequest.getCourseType())
                    .withString("createdDate", CommonUtils.getDateTime())
                    .withString("detailedDescription", createCourseRequest.getDetailedDescription())
                    .withString("skuId", skuId)
                    .withJSON("resources", sectionDetails)));
    }

    private String createProductAndSkuInStripe(BigDecimal amount, String courseName, String coverImage, String formattedCourseName, String imageUrl){
        Float centAmount = amount.floatValue()*100;
        Integer centAmountRounded = centAmount.intValue();
        String image = imageUrl + formattedCourseName+"/"+coverImage;
        System.out.println("Image Location:"+ image);

        Stripe.apiKey = (String) configItem.get("stripeSecretKey");

        Map<String, Object> productParams = new HashMap<>();
        productParams.put("name", courseName);
        productParams.put("type", "good");
        ArrayList attributes = new ArrayList<>();
        attributes.add("name");
        productParams.put("attributes", attributes);
        Product product = null;
        try {
            product = Product.create(productParams);
        } catch (StripeException e) {
            e.printStackTrace();
        }
        System.out.println("Product Id:"+ product.getId());

        Map<String, Object> skuParams = new HashMap<String, Object>();
        skuParams.put("product", product.getId());
        skuParams.put("price", centAmountRounded);
        skuParams.put("currency", "usd");
        Map<String, Object> attributesParams = new HashMap<String, Object>();
        attributesParams.put("name", courseName);
        skuParams.put("attributes", attributesParams);
        Map<String, Object> inventoryParams = new HashMap<String, Object>();
        inventoryParams.put("type", "infinite");
        skuParams.put("inventory", inventoryParams);
        skuParams.put("image", image);

        System.out.println("Image Set in SKU:"+skuParams.get("image"));

        Sku sku = null;
        try {
            sku = Sku.create(skuParams);
        } catch (StripeException e) {
            e.printStackTrace();
        }
        System.out.println("SKUID:"+ sku.getId());
        return sku.getId();
    }
}