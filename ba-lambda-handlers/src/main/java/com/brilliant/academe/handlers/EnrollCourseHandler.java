package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.enrollment.EnrollCourseInfoList;
import com.brilliant.academe.domain.enrollment.EnrollCourseRequest;
import com.brilliant.academe.domain.enrollment.EnrollCourseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

public class EnrollCourseHandler implements RequestHandler<EnrollCourseRequest, EnrollCourseResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_USER = "ba_user";
    private Regions REGION = Regions.US_EAST_1;

    public EnrollCourseResponse handleRequest(EnrollCourseRequest enrollCourseRequest, Context context) {
        this.initDynamoDbClient();
        persistData(enrollCourseRequest);
        EnrollCourseResponse response = new EnrollCourseResponse();
        response.setMessage("SUCCESS");
        return response;
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private UpdateItemOutcome persistData(EnrollCourseRequest enrollCourseRequest)
            throws ConditionalCheckFailedException {

        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_USER);

        Item item = table.getItem("userId", enrollCourseRequest.getUserId());
        String enrolledCoursesJson = new Gson().toJson(item.get("enrolled_courses"));

        EnrollCourseInfoList existingCoursesFromDB = new EnrollCourseInfoList();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            existingCoursesFromDB = objectMapper.readValue(enrolledCoursesJson, EnrollCourseInfoList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("existingCoursesFromDB: "+ existingCoursesFromDB);
        System.out.println("enrollCourseRequest.getCourses(): "+ enrollCourseRequest.getBody().getCourses());
        if(Objects.isNull(existingCoursesFromDB)){
            existingCoursesFromDB = new EnrollCourseInfoList();
            existingCoursesFromDB.setCourses(enrollCourseRequest.getBody().getCourses());
        }else {
            existingCoursesFromDB.getCourses().addAll(enrollCourseRequest.getBody().getCourses());
        }

        System.out.println("GET COURSES: " + existingCoursesFromDB.getCourses());
        String totalCourses = "";
        try {
            totalCourses = objectMapper.writeValueAsString(existingCoursesFromDB);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("userId", enrollCourseRequest.getUserId())
                .withUpdateExpression("set enrolled_courses = :val")
                .withValueMap(new ValueMap()
                        .withJSON(":val", totalCourses));

        return table.updateItem(updateItemSpec);
    }
}
