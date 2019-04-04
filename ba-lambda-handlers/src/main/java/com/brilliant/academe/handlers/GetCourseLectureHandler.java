package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.GetCourseLectureRequest;
import com.brilliant.academe.domain.course.GetCourseLectureResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.DYNAMODB_TABLE_NAME_COURSE_RESOURCE;
import static com.brilliant.academe.constant.Constant.REGION;

public class GetCourseLectureHandler  implements RequestHandler<GetCourseLectureRequest, GetCourseLectureResponse> {

    private DynamoDB dynamoDB;

    @Override
    public GetCourseLectureResponse handleRequest(GetCourseLectureRequest courseRequest, Context context) {
        this.initDynamoDbClient();
        return execute(courseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDB = new DynamoDB(client);
    }

    public GetCourseLectureResponse execute(GetCourseLectureRequest courseRequest){
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", courseRequest.getCourseId())
                .withAttributesToGet("id", "resources");
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem(itemSpec);
        GetCourseLectureResponse response = new GetCourseLectureResponse();
        if(Objects.nonNull(item)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                response = objectMapper.readValue(item.toJSON(), GetCourseLectureResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }
}
