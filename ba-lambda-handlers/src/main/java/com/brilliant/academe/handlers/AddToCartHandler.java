package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.cart.CourseCartRequest;
import com.brilliant.academe.domain.cart.CourseCartResponse;
import com.brilliant.academe.util.CommonUtils;

import static com.brilliant.academe.constant.Constant.*;

public class AddToCartHandler implements RequestHandler<CourseCartRequest, CourseCartResponse> {

    private DynamoDB dynamoDB;

    @Override
    public CourseCartResponse handleRequest(CourseCartRequest request, Context context) {
        initDynamoDbClient();
        return execute(request);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    private CourseCartResponse execute(CourseCartRequest request){
        String userId = CommonUtils.getUserFromToken(request.getToken());
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_CART).putItem(new PutItemSpec().withItem(new Item()
                        .withString("userId", userId)
                        .withString("courseId", request.getCourseId())
                        .withString("cartStatus", STATUS_SAVE)));
        CourseCartResponse response = new CourseCartResponse();
        response.setMessage(STATUS_SUCCESS);
        return response;
    }
}
