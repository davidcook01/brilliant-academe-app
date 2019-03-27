package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.GetCourseRequest;
import com.brilliant.academe.domain.course.GetCourseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class GetCourseHandler implements RequestHandler<GetCourseRequest, GetCourseResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_COURSE_RESOURCE = "ba_course_resource";
    private Regions REGION = Regions.US_EAST_1;

    @Override
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
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE);
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", courseRequest.getCourseId());
        Item item = table.getItem(itemSpec);
        GetCourseResponse courseResponse = new GetCourseResponse();
        try {
            courseResponse = new ObjectMapper().readValue(item.toJSON(), GetCourseResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courseResponse;
    }
}
