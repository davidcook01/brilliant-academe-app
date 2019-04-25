package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.cart.CourseCartRequest;
import com.brilliant.academe.domain.cart.CourseCartResponse;
import com.brilliant.academe.util.CommonUtils;

import java.math.BigDecimal;
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
        ItemCollection<QueryOutcome> userInprocessCartItems = CommonUtils.getUserInprocessCart(userId, dynamoDB);

        String orderId = null;
        for(Item item: userInprocessCartItems){
            orderId = (String) item.get("orderId");
            break;
        }

        if(!Objects.nonNull(orderId)){
            orderId = UUID.randomUUID().toString();
        }

        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem("id", request.getCourseId());
        String skuId = (String) item.get("skuId");
        BigDecimal amount = (BigDecimal) item.get("discountedPrice");
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER_CART).putItem(new PutItemSpec().withItem(new Item()
                .withString("orderId", orderId)
                .withString("userId", userId)
                .withString("courseId", request.getCourseId())
                .withString("skuId", skuId)
                .withNumber("amount", amount)
                .withString("createdDate", CommonUtils.getDateTime())
                .withString("cartStatus", STATUS_IN_PROCESS)));
        CourseCartResponse response = new CourseCartResponse();
        response.setMessage(orderId);
        return response;
    }
}
