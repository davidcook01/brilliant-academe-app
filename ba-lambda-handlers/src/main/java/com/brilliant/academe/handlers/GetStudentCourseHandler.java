package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.GetStudentCourseRequest;
import com.brilliant.academe.domain.course.GetStudentCourseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class GetStudentCourseHandler implements RequestHandler<GetStudentCourseRequest, GetStudentCourseResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_USER = "ba_user";
    private Regions REGION = Regions.US_EAST_1;

    public GetStudentCourseResponse handleRequest(GetStudentCourseRequest studentCourseRequest, Context context) {
        this.initDynamoDbClient();
        return getData(studentCourseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private GetStudentCourseResponse getData(GetStudentCourseRequest getStudentCourseRequest){

        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_USER);
        Item item = table.getItem("userId", getStudentCourseRequest.getUserId());

        GetStudentCourseResponse studentCourseResponse = new GetStudentCourseResponse();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            studentCourseResponse = objectMapper.readValue(item.toJSON(), GetStudentCourseResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return studentCourseResponse;
    }
}
