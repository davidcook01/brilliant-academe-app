package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.brilliant.academe.constant.Constant;
import com.brilliant.academe.domain.course.CourseCategory;
import com.brilliant.academe.domain.instructor.*;
import com.brilliant.academe.util.CommonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.model.Sku;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.brilliant.academe.constant.Constant.*;

public class InstructorCreateCourseHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String TYPE_COURSE = "course";
    private static final String TYPE_IMAGE = "image";
    private static final String TYPE_SECTION = "section";
    private static final String TYPE_LECTURE = "lecture";
    private static final String TYPE_MATERIAL = "material";

    private static final String OPERATION_GET = "get";
    private static final String OPERATION_GET_ALL = "getAll";
    private static final String OPERATION_SUBMIT = "submit";
    private static final String OPERATION_CREATE = "create";
    private static final String OPERATION_UPDATE = "update";
    private static final String OPERATION_DETETE = "delete";

    private DynamoDB dynamoDB;

    private Item instructorItem;

    private Item configItem;

    private String[] attributes = {"cfDistributionName", "stripeSecretKey", "s3ContentUploadFolder", "s3ImageUploadFolder"};

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        initDynamoDbClient();
        InstructorCourseRequest request = null;
        try {
            request = objectMapper.readValue(requestEvent.getBody(), InstructorCourseRequest.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String token = requestEvent.getHeaders().get(Constant.HEADER_AUTHORIZATION);
        InstructorCourseResponse response = execute(token, request);
        return CommonUtils.setResponseBodyAndCorsHeaders(response);
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

    public InstructorCourseResponse execute(String token, InstructorCourseRequest request){
        boolean isInstructor = checkIfInstructor(token);
        InstructorCourseResponse response = new InstructorCourseResponse();
        if(isInstructor){
            response = executeRequest(request);
        }
        return response;
    }

    private boolean checkIfInstructor(String token){
        String userId = CommonUtils.getUserFromToken(token);
        boolean isInstructor = false;
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER).getItem("id", userId);
        if(Objects.nonNull(item) && Objects.nonNull(item.get("instructor"))){
            isInstructor = (Boolean) item.get("instructor");
            instructorItem = item;
        }
        return isInstructor;
    }

    private InstructorCourseResponse executeRequest(InstructorCourseRequest request){
        String type = request.getType();
        switch (type) {
            case TYPE_COURSE:
                return executeCourse(request.getOperation(), request.getCourse());
            case TYPE_IMAGE:
                return executeImage(request.getOperation(), request.getCourse());
            case TYPE_SECTION:
                return executeSection(request.getOperation(), request.getCourse());
            case TYPE_LECTURE:
                return executeLecture(request.getOperation(), request.getCourse());
            case TYPE_MATERIAL:
                return executeMaterial(request.getOperation(), request.getCourse());
            default:
                return null;
        }
    }

    private InstructorCourseResponse executeCourse(String operation, InstructorCourse course){
        InstructorCourseResponse response = new InstructorCourseResponse();
        response.setMessage(STATUS_FAILED);
        switch (operation) {
            case OPERATION_CREATE:
                return createCourse(course, response);
            case OPERATION_UPDATE:
                return updateCourse(course, response);
            case OPERATION_GET:
                return getCourse(course.getCourseId());
            case OPERATION_GET_ALL:
                return getAllCourse();
            case OPERATION_SUBMIT:
                initConfig();
                return submitCourse(course.getCourseId());
            default:
                return response;
        }
    }

    private InstructorCourseResponse getAllCourse(){
        String userId = (String) instructorItem.get("id");
        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE).getIndex("instructorId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("instructorId = :v_instructor_id")
                .withValueMap(new ValueMap()
                        .withString(":v_instructor_id", userId));

        ItemCollection<QueryOutcome> courseItemsList = index.query(querySpec);
        List<InstructorCourseResponseInfo> instructorCourses = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(Item courseItem: courseItemsList){
            try {
                instructorCourses.add(objectMapper.readValue(courseItem.toJSON(), InstructorCourseResponseInfo.class));
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        InstructorCourseResponse instructorCourseResponse = new InstructorCourseResponse();
        instructorCourseResponse.setCourses(filterUniqueCourses(instructorCourses));
        return instructorCourseResponse;
    }

    private List<InstructorCourseResponseInfo> filterUniqueCourses(List<InstructorCourseResponseInfo> courseResponseInfos){
        Set<InstructorCourseResponseInfo> uniqueCourses = new HashSet<>(courseResponseInfos);
        return new ArrayList<>(uniqueCourses);
    }

    private InstructorCourseResponse createCourse(InstructorCourse course, InstructorCourseResponse response){
        String courseId = UUID.randomUUID().toString();
        String userId = (String) instructorItem.get("id");
        String userName = (String) instructorItem.get("fullName");

        dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE)
                .putItem(new PutItemSpec().withItem(new Item()
                        .withString("id", courseId)
                        .withDouble("price", course.getCoursePrice().doubleValue())
                        .withDouble("discountedPrice", course.getDiscountedCoursePrice().doubleValue())
                        .withString("courseName", course.getCourseName())
                        .withString("description", course.getCourseDescription())
                        .withString("courseLevel", course.getCourseLevel())
                        .withString("instructorId", userId)
                        .withString("instructorName", userName)
                        .withNumber("courseDuration", 0) //TODO
                        .withString("courseType", course.getCourseType())
                        .withString("createdDate", CommonUtils.getDateTime())
                        .withString("detailedDescription", course.getDetailedDescription())
                        .withString("tags", course.getTags())
                        .withString("submitted", STATUS_NO)
                        .withString("reviewed", STATUS_NO)));

        Table courseTable = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE);
        for(CourseCategory courseCategory: course.getCourseCategories()){
            courseTable.putItem(new PutItemSpec().withItem(new Item()
                    .withString("id", courseId)
                    .withString("courseName", course.getCourseName())
                    .withString("description", course.getCourseDescription())
                    .withString("courseLevel", course.getCourseLevel())
                    .withDouble("price", course.getCoursePrice().doubleValue())
                    .withDouble("discountedPrice", course.getDiscountedCoursePrice().doubleValue())
                    .withString("instructorId", userId)
                    .withString("instructorName", userName)
                    .withString("categoryId", courseCategory.getCourseCategoryId())
                    .withString("categoryName", courseCategory.getCourseCategoryName())
                    .withString("categoryDescription", courseCategory.getCourseCategoryDescription())
                    .withNumber("courseDuration", 0) //TODO
                    .withString("courseType", course.getCourseType())
                    .withString("reviewed", STATUS_NO)));
        }

        response.setMessage(STATUS_SUCCESS);
        response.setId(courseId);
        return response;
    }

    private InstructorCourseResponse updateCourse(InstructorCourse course, InstructorCourseResponse response){
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("id", course.getCourseId())
                .withUpdateExpression("set price = :price, discountedPrice=:discountedPrice, " +
                        "courseName = :courseName, description = :description, " +
                        "courseLevel = :courseLevel, courseType = :courseType, " +
                        "detailedDescription = :detailedDescription, tags = :tags, modifiedDate = :modifiedDate")
                .withValueMap(new ValueMap()
                        .withNumber(":price", course.getCoursePrice())
                        .withNumber(":discountedPrice", course.getDiscountedCoursePrice())
                        .withString(":courseName", course.getCourseName())
                        .withString(":description", course.getCourseDescription())
                        .withString(":courseLevel", course.getCourseLevel())
                        .withString(":courseType", course.getCourseType())
                        .withString(":detailedDescription", course.getDetailedDescription())
                        .withString(":courseType", course.getCourseType())
                        .withString(":tags", course.getTags())
                        .withString(":modifiedDate", CommonUtils.getDateTime()));

        dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).updateItem(updateItemSpec);

        ItemCollection<QueryOutcome> items = CommonUtils.getCoursesByCourseIdInMaster(course.getCourseId(), dynamoDB);
        for(Item item: items){
            String updateCourseId = (String) item.get("id");
            String updateCategoryId = (String) item.get("categoryId");

            UpdateItemSpec updateItemSpecCourse = new UpdateItemSpec()
                    .withPrimaryKey("id", updateCourseId, "categoryId", updateCategoryId)
                    .withUpdateExpression("set price = :price, discountedPrice=:discountedPrice, " +
                            "courseName = :courseName, description = :description, " +
                            "courseLevel = :courseLevel, courseType = :courseType, " +
                            "detailedDescription = :detailedDescription, modifiedDate = :modifiedDate")
                    .withValueMap(new ValueMap()
                            .withNumber(":price", course.getCoursePrice())
                            .withNumber(":discountedPrice", course.getDiscountedCoursePrice())
                            .withString(":courseName", course.getCourseName())
                            .withString(":description", course.getCourseDescription())
                            .withString(":courseLevel", course.getCourseLevel())
                            .withString(":courseType", course.getCourseType())
                            .withString(":detailedDescription", course.getDetailedDescription())
                            .withString(":modifiedDate", CommonUtils.getDateTime()));

            dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE).updateItem(updateItemSpecCourse);
        }
        response.setMessage(STATUS_SUCCESS);
        return response;
    }

    private InstructorCourseResponse getCourse(String courseId){
        String[] attributes = {"id", "courseLevel", "courseName",
                "courseType", "coverImage", "description", "discountedPrice",
                "price", "detailedDescription", "tags"};
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", courseId)
                .withAttributesToGet(attributes);
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem(itemSpec);
        InstructorCourseResponseInfo course = new InstructorCourseResponseInfo();
        if(Objects.nonNull(item)){
            try {
                course = new ObjectMapper().readValue(item.toJSON(), InstructorCourseResponseInfo.class);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InstructorCourseResponse response = new InstructorCourseResponse();
        response.setCourse(course);
        return response;
    }

    private InstructorCourseResponse submitCourse(String courseId){
        InstructorCourseResponse response = new InstructorCourseResponse();
        response.setMessage(STATUS_FAILED);

        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", courseId)
                .withAttributesToGet("id", "courseName", "coverImage", "discountedPrice");
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem(itemSpec);
        String courseName = (String) item.get("courseName");
        BigDecimal amount = (BigDecimal) item.get("discountedPrice");
        String coverImage = (String) item.get("coverImage");

        String skuId = createProductAndSkuInStripe(courseName, amount, coverImage);
        boolean isSubmit = updateSkuIdAndSubmit(courseId, skuId);
        if(isSubmit) {
            response.setMessage(STATUS_SUCCESS);
        }
        return response;
    }

    private String createProductAndSkuInStripe(String courseName, BigDecimal amount, String coverImageUrl){
        Float centAmount = amount.floatValue()*100;
        Integer centAmountRounded = centAmount.intValue();

        Stripe.apiKey = (String) configItem.get("stripeSecretKey");
        String skuId = null;

        Map<String, Object> productParams = new HashMap<>();
        productParams.put("name", courseName);
        productParams.put("type", "good");
        ArrayList attributes = new ArrayList<>();
        attributes.add("name");
        productParams.put("attributes", attributes);
        Product product = null;
        try {
            product = Product.create(productParams);
            Map<String, Object> skuParams = new HashMap<>();
            skuParams.put("product", product.getId());
            skuParams.put("price", centAmountRounded);
            skuParams.put("currency", "usd");
            Map<String, Object> attributesParams = new HashMap<>();
            attributesParams.put("name", courseName);
            skuParams.put("attributes", attributesParams);
            Map<String, Object> inventoryParams = new HashMap<>();
            inventoryParams.put("type", "infinite");
            skuParams.put("inventory", inventoryParams);
            skuParams.put("image", coverImageUrl);

            System.out.println("Image Set in SKU:"+skuParams.get("image"));

            Sku sku = null;
            try {
                sku = Sku.create(skuParams);
                skuId = sku.getId();
            } catch (StripeException e) {
                e.printStackTrace();
            }
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return skuId;
    }

    private boolean updateSkuIdAndSubmit(String courseId, String skuId){
        UpdateItemSpec updateItemSpecCourse = new UpdateItemSpec()
                .withPrimaryKey("id", courseId)
                .withUpdateExpression("set skuId = :skuId, modifiedDate = :modifiedDate, submitted = :submitted")
                .withValueMap(new ValueMap()
                        .withString(":skuId", skuId)
                        .withString(":submitted", STATUS_YES)
                        //.withString(":reviewed", STATUS_YES) //TODO remove this line
                        .withString(":modifiedDate", CommonUtils.getDateTime()));
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).updateItem(updateItemSpecCourse);
        return true;
    }

    private InstructorCourseResponse executeImage(String operation, InstructorCourse course){
        InstructorCourseResponse response = new InstructorCourseResponse();
        response.setMessage(STATUS_FAILED);
        if(operation.equals(OPERATION_CREATE)){
            initConfig();
            UpdateItemSpec updateItemSpec = getUpdateItemSpec(course);
            dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).updateItem(updateItemSpec);

            ItemCollection<QueryOutcome> items = CommonUtils.getCoursesByCourseIdInMaster(course.getCourseId(), dynamoDB);
            for(Item item: items){
                String updateCourseId = (String) item.get("id");
                String updateCategoryId = (String) item.get("categoryId");

                String coverImage = getCoverImageLocation(course.getCourseId(), course.getCoverImage());
                UpdateItemSpec updateItemSpecCourse = new UpdateItemSpec()
                        .withPrimaryKey("id", updateCourseId, "categoryId", updateCategoryId)
                        .withUpdateExpression("set coverImage = :coverImage, modifiedDate = :modifiedDate")
                        .withValueMap(new ValueMap()
                                .withString(":coverImage", coverImage)
                                .withString(":modifiedDate", CommonUtils.getDateTime()));

                dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE).updateItem(updateItemSpecCourse);
            }
            response.setMessage(STATUS_SUCCESS);
        }
        return response;
    }

    private String getCoverImageLocation(String courseId, String coverImage){
        String cfDistributionName = (String) configItem.get("cfDistributionName");
        String CF_IMAGE_URL = "https://" + cfDistributionName + "/" + CF_IMAGES_ORIGIN_PATH +"/";
        return CF_IMAGE_URL + courseId + "/" + coverImage;
    }

    private UpdateItemSpec getUpdateItemSpec(InstructorCourse course){
        String coverImage = getCoverImageLocation(course.getCourseId(), course.getCoverImage());
        return new UpdateItemSpec()
                .withPrimaryKey("id", course.getCourseId())
                .withUpdateExpression("set coverImage = :coverImage, modifiedDate = :modifiedDate")
                .withValueMap(new ValueMap()
                        .withString(":coverImage", coverImage)
                        .withString(":modifiedDate", CommonUtils.getDateTime()));
    }

    private InstructorCourseResponseInfo getCourseDetails(String courseId){
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", courseId)
                .withAttributesToGet("id", "resources");
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem(itemSpec);

        InstructorCourseResponseInfo courseDetails = new InstructorCourseResponseInfo();
        if(Objects.nonNull(item)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                courseDetails = objectMapper.readValue(item.toJSON(), InstructorCourseResponseInfo.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return courseDetails;
    }

    private void updateSectionDetails(String courseId, List<InstructorCourseSection> sections){
        String sectionDetails = "NA";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            sectionDetails = objectMapper.writeValueAsString(sections);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("id", courseId)
                .withUpdateExpression("set resources = :resources, modifiedDate = :modifiedDate")
                .withValueMap(new ValueMap()
                        .withJSON(":resources", sectionDetails)
                        .withString(":modifiedDate", CommonUtils.getDateTime()));
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).updateItem(updateItemSpec);
    }

    private InstructorCourseResponse executeSection(String operation, InstructorCourse course){
        InstructorCourseResponse response = new InstructorCourseResponse();
        response.setMessage(STATUS_FAILED);
        if(operation.equals(OPERATION_CREATE)) {
            String sectionId = UUID.randomUUID().toString();
            InstructorCourseResponseInfo courseDetails = getCourseDetails(course.getCourseId());
            if(Objects.isNull(courseDetails.getSections()) || courseDetails.getSections().size() == 0){
                courseDetails.setSections(new ArrayList<>());
            }
            InstructorCourseSection section = course.getSections().get(0);
            section.setSectionId(sectionId);
            courseDetails.getSections().add(section);
            updateSectionDetails(course.getCourseId(), courseDetails.getSections());
            response.setMessage(STATUS_SUCCESS);
            response.setId(sectionId);
        } else if(operation.equals(OPERATION_GET)) {
            InstructorCourseResponseInfo courseDetails = getCourseDetails(course.getCourseId());
            response.setSections(courseDetails.getSections());
            response.setMessage(STATUS_SUCCESS);
        }else if(operation.equals(OPERATION_DETETE)) {
            initConfig();
            InstructorCourseResponseInfo courseDetails = getCourseDetails(course.getCourseId());
            String sectionId = course.getSections().get(0).getSectionId();

            InstructorCourseSection courseSection = courseDetails.getSections().stream()
                    .filter(cs -> sectionId.equals(cs.getSectionId())).findAny().orElse(null);

            if(Objects.nonNull(courseSection) && Objects.nonNull(courseSection.getLectures())
                    && courseSection.getLectures().size() > 0){
                for(InstructorCourseLecture cl: courseSection.getLectures()){
                    if(Objects.nonNull(cl.getMaterials()) && cl.getMaterials().size() > 0){
                        for(InstructorCourseMaterial cm: cl.getMaterials()){
                            String extension = getFileExtenstion(cm.getMaterialFile());
                            deleteObjectFromS3(course.getCourseId(), cm.getMaterialId(), extension, TYPE_MATERIAL);
                        }
                    }
                    deleteObjectFromS3(course.getCourseId(), cl.getLectureId(), Constant.HLS_FORMAT, TYPE_LECTURE);
                }
            }

            courseDetails.getSections().remove(courseSection);
            if(courseDetails.getSections().size() == 0){
                courseDetails.setSections(null);
            }
            updateSectionDetails(course.getCourseId(), courseDetails.getSections());
            response.setMessage(STATUS_SUCCESS);
        }
        return response;
    }

    private InstructorCourseResponse executeLecture(String operation, InstructorCourse course){
        InstructorCourseResponse response = new InstructorCourseResponse();
        response.setMessage(STATUS_FAILED);
        if(operation.equals(OPERATION_CREATE)) {
            String lectureId = UUID.randomUUID().toString();
            response.setMessage(STATUS_SUCCESS);
            response.setId(lectureId);
        }else if(operation.equals(OPERATION_UPDATE)) {
            InstructorCourseResponseInfo courseDetails = getCourseDetails(course.getCourseId());
            String sectionId = course.getSections().get(0).getSectionId();
            InstructorCourseSection courseSection = courseDetails.getSections().stream()
                    .filter(cs -> sectionId.equals(cs.getSectionId())).findAny().orElse(null);
            courseDetails.getSections().remove(courseSection);
            if(Objects.nonNull(courseSection)){
                if(Objects.isNull(courseSection.getLectures()) || courseSection.getLectures().size() == 0){
                    courseSection.setLectures(new ArrayList<>());
                }
                InstructorCourseLecture lecture = course.getSections().get(0).getLectures().get(0);
                lecture.setLectureLink(CF_VIDEOS_ORIGIN_PATH + "/" + course.getCourseId() + "/"+ lecture.getLectureId() + HLS_M3U8_FORMAT);
                courseSection.getLectures().add(lecture);
                courseDetails.getSections().add(courseSection);
                updateSectionDetails(course.getCourseId(), courseDetails.getSections());
                response.setMessage(STATUS_SUCCESS);
            }
        } else if(operation.equals(OPERATION_DETETE)) {
            initConfig();
            InstructorCourseResponseInfo courseDetails = getCourseDetails(course.getCourseId());
            String sectionId = course.getSections().get(0).getSectionId();
            String lectureId = course.getSections().get(0).getLectures().get(0).getLectureId();

            InstructorCourseSection courseSection = courseDetails.getSections().stream()
                    .filter(cs -> sectionId.equals(cs.getSectionId())).findAny().orElse(null);

            InstructorCourseLecture courseLecture = courseSection.getLectures().stream()
                    .filter(cl -> lectureId.equals(cl.getLectureId())).findAny().orElse(null);

           if(Objects.nonNull(courseLecture) && Objects.nonNull(courseLecture.getMaterials())
                   && courseLecture.getMaterials().size() > 0){
               for(InstructorCourseMaterial cm: courseLecture.getMaterials()){
                   String extension = getFileExtenstion(cm.getMaterialFile());
                   deleteObjectFromS3(course.getCourseId(), cm.getMaterialId(), extension, TYPE_MATERIAL);
               }
           }

            courseSection.getLectures().remove(courseLecture);
            if(courseSection.getLectures().size() == 0){
                courseSection.setLectures(null);
            }

            updateSectionDetails(course.getCourseId(), courseDetails.getSections());
            deleteObjectFromS3(course.getCourseId(), lectureId, Constant.HLS_FORMAT, TYPE_LECTURE);
            response.setMessage(STATUS_SUCCESS);
        }
        return response;
    }

    private InstructorCourseResponse executeMaterial(String operation, InstructorCourse course){
        InstructorCourseResponse response = new InstructorCourseResponse();
        response.setMessage(STATUS_FAILED);
        if(operation.equals(OPERATION_CREATE)) {
            String materialId = UUID.randomUUID().toString();
            response.setMessage(STATUS_SUCCESS);
            response.setId(materialId);
        }
        if(operation.equals(OPERATION_UPDATE)) {
            InstructorCourseResponseInfo courseDetails = getCourseDetails(course.getCourseId());
            String sectionId = course.getSections().get(0).getSectionId();
            String lectureId = course.getSections().get(0).getLectures().get(0).getLectureId();
            InstructorCourseSection courseSection = courseDetails.getSections().stream()
                    .filter(cs -> sectionId.equals(cs.getSectionId())).findAny().orElse(null);
            courseDetails.getSections().remove(courseSection);
            InstructorCourseLecture courseLecture = courseSection.getLectures().stream()
                    .filter(cl -> lectureId.equals(cl.getLectureId())).findAny().orElse(null);

            if(Objects.nonNull(courseLecture)){
                if(Objects.isNull(courseLecture.getMaterials()) || courseLecture.getMaterials().size() == 0){
                    courseLecture.setMaterials(new ArrayList<>());
                }
                InstructorCourseMaterial material = course.getSections().get(0).getLectures().get(0).getMaterials().get(0);
                material.setMaterialLink(CF_VIDEOS_ORIGIN_PATH + "/" + course.getCourseId() + "/"+ material.getMaterialLink());
                courseLecture.getMaterials().add(material);
                Set<InstructorCourseLecture> uniqueLectures = new HashSet<InstructorCourseLecture>();
                uniqueLectures.addAll(courseSection.getLectures());
                uniqueLectures.add(courseLecture);
                List<InstructorCourseLecture> lectures = new ArrayList<InstructorCourseLecture>();
                lectures.addAll(uniqueLectures);
                courseSection.setLectures(lectures);
                courseDetails.getSections().add(courseSection);
                updateSectionDetails(course.getCourseId(), courseDetails.getSections());
                response.setMessage(STATUS_SUCCESS);
            }
        }
        if(operation.equals(OPERATION_DETETE)) {
            initConfig();
            InstructorCourseResponseInfo courseDetails = getCourseDetails(course.getCourseId());
            String sectionId = course.getSections().get(0).getSectionId();
            String lectureId = course.getSections().get(0).getLectures().get(0).getLectureId();
            String materialId = course.getSections().get(0).getLectures().get(0).getMaterials().get(0).getMaterialId();
            String materialFile = course.getSections().get(0).getLectures().get(0).getMaterials().get(0).getMaterialFile();

            InstructorCourseSection courseSection = courseDetails.getSections().stream()
                    .filter(cs -> sectionId.equals(cs.getSectionId())).findAny().orElse(null);

            InstructorCourseLecture courseLecture = courseSection.getLectures().stream()
                    .filter(cl -> lectureId.equals(cl.getLectureId())).findAny().orElse(null);

            InstructorCourseMaterial courseMaterial = courseLecture.getMaterials().stream()
                    .filter(cm -> materialId.equals(cm.getMaterialId())).findAny().orElse(null);
            courseLecture.getMaterials().remove(courseMaterial);
            if(courseLecture.getMaterials().size() == 0){
                courseLecture.setMaterials(null);
            }
            updateSectionDetails(course.getCourseId(), courseDetails.getSections());
            String extension = getFileExtenstion(materialFile);
            deleteObjectFromS3(course.getCourseId(), materialId, extension, TYPE_MATERIAL);
            response.setMessage(STATUS_SUCCESS);
        }
        return response;
    }

    private String getFileExtenstion(String materialFile){
        Optional<String> s = Optional.ofNullable(materialFile)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(materialFile.lastIndexOf(".") + 1));
        String extension = null;
        if(s.isPresent()){
            extension = s.get();
        }
        return extension;
    }

    private void deleteObjectFromS3(String courseId, String id, String format, String type){
        String clientRegion = Constant.REGION.US_EAST_1.getName();
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(clientRegion).build();
        String keyName = null;
        String bucketName = (String) configItem.get("s3ContentUploadFolder");

        if(type.equals(TYPE_MATERIAL)){
            keyName = CF_VIDEOS_ORIGIN_PATH + "/" + courseId + "/" + id + "." + format;
            CommonUtils.deleteS3Object(s3Client, bucketName, keyName);
        }

        if(type.equals(TYPE_LECTURE)){
            String prefix = CF_VIDEOS_ORIGIN_PATH + "/" + courseId + "/";
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix);
            List<String> keys = new ArrayList<>();
            ObjectListing objects = s3Client.listObjects(listObjectsRequest);
            for (;;) {
                List<S3ObjectSummary> summaries = objects.getObjectSummaries();
                if (summaries.size() < 1) {
                    break;
                }
                summaries.forEach(s -> keys.add(s.getKey()));
                objects = s3Client.listNextBatchOfObjects(objects);
            }

            for(String key: keys){
                if(key.contains(prefix + id)){
                    CommonUtils.deleteS3Object(s3Client, bucketName, key);
                }
            }
        }

        if(type.equals(TYPE_IMAGE)){
            String imageBucketName = (String) configItem.get("s3ImageUploadFolder");
            String prefix = CF_IMAGES_ORIGIN_PATH + "/" + courseId + "/";
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest().withBucketName(bucketName).withPrefix(prefix);
            ObjectListing objects = s3Client.listObjects(listObjectsRequest);
            for (;;) {
                List<S3ObjectSummary> summaries = objects.getObjectSummaries();
                if (summaries.size() < 1) {
                    break;
                }
                summaries.forEach(s -> CommonUtils.deleteS3Object(s3Client, imageBucketName, s.getKey()));
                objects = s3Client.listNextBatchOfObjects(objects);
            }
        }
    }
}