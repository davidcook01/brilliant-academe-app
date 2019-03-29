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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.brilliant.academe.constant.Constant.DYNAMODB_TABLE_NAME_COURSE_RESOURCE;
import static com.brilliant.academe.constant.Constant.REGION;

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
        this.dynamoDB = new DynamoDB(client);
    }

    public GetCourseResponse execute(GetCourseRequest courseRequest){
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", courseRequest.getCourseId());
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem(itemSpec);
        GetCourseResponse courseResponse = new GetCourseResponse();
        try {
            courseResponse = new ObjectMapper().readValue(item.toJSON(), GetCourseResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courseResponse;
    }
}
