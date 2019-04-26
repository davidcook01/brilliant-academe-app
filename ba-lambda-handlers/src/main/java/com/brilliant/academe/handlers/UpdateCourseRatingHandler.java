package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.common.CommonResponse;
import com.brilliant.academe.domain.rating.UpdateCourseRatingRequest;
import com.brilliant.academe.util.CommonUtils;

import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class UpdateCourseRatingHandler implements RequestHandler<UpdateCourseRatingRequest, CommonResponse> {

    private DynamoDB dynamoDB;

    @Override
    public CommonResponse handleRequest(UpdateCourseRatingRequest updateCourseRatingRequest, Context context) {
        initDynamoDbClient();
        return execute(updateCourseRatingRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public CommonResponse execute(UpdateCourseRatingRequest updateCourseRatingRequest) {
        String userId = CommonUtils.getUserFromToken(updateCourseRatingRequest.getToken());
        CommonResponse response = new CommonResponse();
        response.setMessage(STATUS_FAILED);
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER_COURSE).getItem("userId", userId, "courseId", updateCourseRatingRequest.getCourseId());
        if(Objects.nonNull(item)){
            Float courseRating = updateCourseRatingRequest.getBody().getCourseRating();
            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("userId", userId, "courseId", updateCourseRatingRequest.getCourseId())
                    .withUpdateExpression("set #p = :courseRating")
                    .withNameMap(new NameMap().with("#p", "courseRating"))
                    .withValueMap(new ValueMap().withNumber(":courseRating", courseRating));
            dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER_COURSE).updateItem(updateItemSpec);
            response.setMessage(STATUS_SUCCESS);
        }
        return response;
    }
}
