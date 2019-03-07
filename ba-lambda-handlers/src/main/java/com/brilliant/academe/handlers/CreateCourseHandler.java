package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.CreateCourseRequest;
import com.brilliant.academe.domain.course.CreateCourseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

public class CreateCourseHandler implements RequestHandler<CreateCourseRequest, CreateCourseResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_COURSE = "ba_course";
    private Regions REGION = Regions.US_EAST_1;
    private String S3_UPLOAD_FOLDER = "https://s3.amazonaws.com/brilliant-academe-video-upload/";

    public CreateCourseResponse handleRequest(CreateCourseRequest createCourseRequest, Context context) {
        this.initDynamoDbClient();
        persistData(createCourseRequest);
        CreateCourseResponse response = new CreateCourseResponse();
        response.setMessage("SUCCESS");
        return response;
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private PutItemOutcome persistData(CreateCourseRequest createCourseRequest)
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

        String sectionDetails = "";
        String courseCategories = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            sectionDetails = objectMapper.writeValueAsString(createCourseRequest.getSections());
            courseCategories = objectMapper.writeValueAsString(createCourseRequest.getCourseCategories());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return this.dynamoDb.getTable(DYNAMODB_TABLE_NAME_COURSE)
                .putItem(
                        new PutItemSpec().withItem(new Item()
                                .withString("courseId", UUID.randomUUID().toString())
                                .withString("instructorId", createCourseRequest.getInstructorId())
                                .withString("instructorName", createCourseRequest.getInstructorName())
                                .withString("courseName", createCourseRequest.getCourseName())
                                .withString("courseDescription", createCourseRequest.getCourseDescription())
                                .withString("courseRating", createCourseRequest.getMyRating())
                                .withJSON("courseCategories", courseCategories)
                                .withString("courseCoverImage", S3_UPLOAD_FOLDER + createCourseRequest.getCourseName() + createCourseRequest.getCoverImage())
                                .withJSON("courseSection", sectionDetails)));
    }

}
