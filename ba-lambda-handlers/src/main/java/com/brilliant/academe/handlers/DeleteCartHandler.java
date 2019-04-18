package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.cart.CourseCartRequest;
import com.brilliant.academe.domain.cart.CourseCartResponse;
import com.brilliant.academe.util.CommonUtils;

import static com.brilliant.academe.constant.Constant.*;

public class DeleteCartHandler implements RequestHandler<CourseCartRequest, CourseCartResponse> {

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

    public CourseCartResponse execute(CourseCartRequest request){
        CourseCartResponse response = new CourseCartResponse();
        response.setMessage(STATUS_FAILED);

        String userId = CommonUtils.getUserFromToken(request.getToken());
        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER_CART).getIndex("userId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("userId = :v_user_id")
                .withFilterExpression("cartStatus = :v_cart_status")
                .withValueMap(new ValueMap()
                        .withString(":v_user_id", userId)
                        .withString(":v_cart_status", STATUS_IN_PROCESS));

        String orderId = "";
        String tableUserId = "";
        boolean isCourseAvailableIncart = false;
        for(Item item: index.query(querySpec)){
            orderId = (String) item.get("orderId");
            tableUserId = (String) item.get("userId");
            isCourseAvailableIncart = true;
            break;
        }
        if(isCourseAvailableIncart && tableUserId.equals(userId)){
            dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER_CART).deleteItem("orderId", orderId,
                    "courseId", request.getCourseId());
            response.setMessage(STATUS_SUCCESS);
        }
        return response;
    }
}