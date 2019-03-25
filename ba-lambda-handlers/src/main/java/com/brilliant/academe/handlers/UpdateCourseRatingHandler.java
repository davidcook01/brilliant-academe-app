package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.rating.UpdateCourseRatingRequest;
import com.brilliant.academe.domain.rating.UpdateCourseRatingResponse;

public class UpdateCourseRatingHandler implements RequestHandler<UpdateCourseRatingRequest, UpdateCourseRatingResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_USER_COURSE = "ba_user_course";
    private Regions REGION = Regions.US_EAST_1;

    @Override
    public UpdateCourseRatingResponse handleRequest(UpdateCourseRatingRequest updateCourseRatingRequest, Context context) {
        this.initDynamoDbClient();
        execute(updateCourseRatingRequest);
        UpdateCourseRatingResponse response = new UpdateCourseRatingResponse();
        response.setMessage("SUCCESS");
        return response;
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private UpdateItemOutcome execute(UpdateCourseRatingRequest updateCourseRatingRequest) {
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_USER_COURSE);
        Float courseRating = updateCourseRatingRequest.getBody().getCourseRating();
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("userId", updateCourseRatingRequest.getUserId(), "courseId", updateCourseRatingRequest.getCourseId())
                .withUpdateExpression("set #p = :courseRating")
                .withNameMap(new NameMap().with("#p", "courseRating"))
                .withValueMap(new ValueMap().withNumber(":courseRating", courseRating));
        return table.updateItem(updateItemSpec);
    }
}
