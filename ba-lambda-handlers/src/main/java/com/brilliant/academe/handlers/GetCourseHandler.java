package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.GetCourseRequest;
import com.brilliant.academe.domain.course.GetCourseResponse;
import com.brilliant.academe.domain.user.Instructor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class GetCourseHandler implements RequestHandler<GetCourseRequest, GetCourseResponse> {

    private DynamoDB dynamoDB;

    @Override
    public GetCourseResponse handleRequest(GetCourseRequest courseRequest, Context context) {
        initDynamoDbClient();
        return execute(courseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public GetCourseResponse execute(GetCourseRequest courseRequest){
        String[] attributes = {"id", "courseDuration", "courseLevel", "courseName",
                "courseType", "coverImage", "description", "discountedPrice",
                "instructorId", "price", "courseRating", "skuId",
                "totalRating", "totalEnrolled", "detailedDescription"};
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", courseRequest.getCourseId())
                .withAttributesToGet(attributes);
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem(itemSpec);
        String instructorId = (String) item.get("instructorId");
        GetCourseResponse courseResponse = new GetCourseResponse();
        if(Objects.nonNull(item)){
            try {
                courseResponse = new ObjectMapper().readValue(item.toJSON(), GetCourseResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Instructor instructorDetails = getInstructorDetails(instructorId);
        if(Objects.nonNull(instructorDetails))
            courseResponse.setInstructorDetails(instructorDetails);
        return courseResponse;
    }

    public Instructor getInstructorDetails(String userId){
        String[] attributes = {"fullName", "profileImage", "instructorDetails"};
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", userId)
                .withAttributesToGet(attributes);
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER).getItem(itemSpec);
        Instructor instructor = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = new Gson().toJson(item.get("instructorDetails"));
        try {
            instructor = objectMapper.readValue(json, Instructor.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        instructor.setInstructorName((String) item.get("fullName"));
        instructor.setProfileImage((String) item.get("profileImage"));
        return instructor;
    }
}
