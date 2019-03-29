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
import com.brilliant.academe.domain.course.CourseCategory;
import com.brilliant.academe.domain.course.CreateCourseRequest;
import com.brilliant.academe.domain.course.CreateCourseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static com.brilliant.academe.constant.Constant.*;

public class CreateCourseHandler implements RequestHandler<CreateCourseRequest, CreateCourseResponse> {

    private DynamoDB dynamoDB;
    private String courseId;

    @Override
    public CreateCourseResponse handleRequest(CreateCourseRequest createCourseRequest, Context context) {
        initDynamoDbClient();
        return execute(createCourseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public CreateCourseResponse execute(CreateCourseRequest createCourseRequest){
        persistData(createCourseRequest);
        CreateCourseResponse response = new CreateCourseResponse();
        response.setMessage(courseId);
        return response;
    }

    private void persistData(CreateCourseRequest createCourseRequest)
            throws ConditionalCheckFailedException {

        if(createCourseRequest.getSections() != null && createCourseRequest.getSections().size() > 0){
            createCourseRequest.getSections().forEach(section -> {
                if(section.getLectures() != null && section.getLectures().size() > 0){
                    section.getLectures().forEach(lecture->{
                        lecture.setLectureLink(S3_UPLOAD_FOLDER+createCourseRequest.getCourseName()+"/"+lecture.getLectureLink());
                        if(lecture.getMaterials()!= null && lecture.getMaterials().size() > 0) {
                            lecture.getMaterials().forEach(material -> {
                                material.setMaterialLink(S3_UPLOAD_FOLDER + createCourseRequest.getCourseName() + "/" + material.getMaterialLink());
                            });
                        }
                    });
                }
            });
        }

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
                    .withString("coverImage", S3_UPLOAD_FOLDER + createCourseRequest.getCourseName() + "/" + createCourseRequest.getCoverImage())
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

        dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE)
                .putItem(new PutItemSpec().withItem(new Item()
                    .withString("id", courseId)
                    .withDouble("price", createCourseRequest.getCoursePrice().doubleValue())
                    .withDouble("discountedPrice", createCourseRequest.getDiscountedCoursePrice().doubleValue())
                    .withString("courseName", createCourseRequest.getCourseName())
                    .withString("description", createCourseRequest.getCourseDescription())
                    .withString("coverImage", S3_UPLOAD_FOLDER + createCourseRequest.getCourseName() + "/" + createCourseRequest.getCoverImage())
                    .withString("courseLevel", createCourseRequest.getCourseLevel())
                    .withString("instructorId", createCourseRequest.getInstructorId())
                    .withString("instructorName", createCourseRequest.getInstructorName())
                    .withNumber("courseDuration", createCourseRequest.getCourseDuration())
                    .withString("courseType", createCourseRequest.getCourseType())
                    .withJSON("resources", sectionDetails)));
    }
}
