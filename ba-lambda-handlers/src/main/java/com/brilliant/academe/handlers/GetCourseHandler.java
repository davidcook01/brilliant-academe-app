package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.GetCourseRequest;
import com.brilliant.academe.domain.course.GetCourseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class GetCourseHandler implements RequestHandler<GetCourseRequest, GetCourseResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_COURSE = "ba_course";
    private Regions REGION = Regions.US_EAST_1;

    public GetCourseResponse handleRequest(GetCourseRequest courseRequest, Context context) {
        this.initDynamoDbClient();
        return getData(courseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private GetCourseResponse getData(GetCourseRequest courseRequest){

        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_COURSE);
        Item item = table.getItem("courseId", courseRequest.getCourseId());
        GetCourseResponse response = new GetCourseResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = objectMapper.readValue(item.toJSON(), GetCourseResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
