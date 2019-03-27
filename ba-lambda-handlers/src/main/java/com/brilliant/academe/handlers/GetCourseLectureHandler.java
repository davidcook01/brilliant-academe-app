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
import com.brilliant.academe.domain.course.GetCourseLectureRequest;
import com.brilliant.academe.domain.course.GetCourseLectureResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class GetCourseLectureHandler  implements RequestHandler<GetCourseLectureRequest, GetCourseLectureResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_COURSE_RESOURCE = "ba_course_resource";
    private Regions REGION = Regions.US_EAST_1;

    @Override
    public GetCourseLectureResponse handleRequest(GetCourseLectureRequest courseRequest, Context context) {
        this.initDynamoDbClient();
        return getData(courseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private GetCourseLectureResponse getData(GetCourseLectureRequest courseRequest){
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE);
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", courseRequest.getCourseId())
                .withAttributesToGet("id", "resources");
        Item item = table.getItem(itemSpec);
        GetCourseLectureResponse response = new GetCourseLectureResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = objectMapper.readValue(item.toJSON(), GetCourseLectureResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
