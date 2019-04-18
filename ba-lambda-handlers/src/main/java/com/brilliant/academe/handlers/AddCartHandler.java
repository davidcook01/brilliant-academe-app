package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Index;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.cart.CourseCartRequest;
import com.brilliant.academe.domain.cart.CourseCartResponse;
import com.brilliant.academe.util.CommonUtils;

import java.util.Objects;
import java.util.UUID;

import static com.brilliant.academe.constant.Constant.*;

public class AddCartHandler implements RequestHandler<CourseCartRequest, CourseCartResponse> {

    private DynamoDB dynamoDB;

    @Override
    public CourseCartResponse handleRequest(CourseCartRequest courseCartRequest, Context context) {
        initDynamoDbClient();
        return execute(courseCartRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    private CourseCartResponse execute(CourseCartRequest request){
        String userId = CommonUtils.getUserFromToken(request.getToken());

        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER_CART).getIndex("userId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("userId = :v_user_id")
                .withFilterExpression("cartStatus = :v_cart_status")
                .withValueMap(new ValueMap()
                        .withString(":v_user_id", userId)
                        .withString(":v_cart_status", STATUS_IN_PROCESS));

        String orderId = null;
        for(Item item: index.query(querySpec)){
            orderId = (String) item.get("orderId");
            break;
        }
        CourseCartResponse response = new CourseCartResponse();
        if(!Objects.nonNull(orderId)){
            orderId = UUID.randomUUID().toString();
        }

        String skuId = (String) dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem("id", request.getCourseId()).get("skuId");
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER_CART).putItem(new PutItemSpec().withItem(new Item()
                .withString("orderId", orderId)
                .withString("userId", userId)
                .withString("courseId", request.getCourseId())
                .withString("skuId", skuId)
                .withString("cartStatus", STATUS_IN_PROCESS)));

        response.setMessage(orderId);
        return response;
    }
}
