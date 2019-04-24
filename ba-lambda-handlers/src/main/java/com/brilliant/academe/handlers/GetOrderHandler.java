package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.order.GetOrderRequest;
import com.brilliant.academe.domain.order.GetOrderResponse;
import com.brilliant.academe.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.DYNAMODB_TABLE_NAME_ORDER;
import static com.brilliant.academe.constant.Constant.REGION;

public class GetOrderHandler implements RequestHandler<GetOrderRequest, GetOrderResponse> {

    private DynamoDB dynamoDB;

    @Override
    public GetOrderResponse handleRequest(GetOrderRequest getOrderRequest, Context context) {
        initDynamoDbClient();
        return execute(getOrderRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public GetOrderResponse execute(GetOrderRequest orderRequest){
        String userId = CommonUtils.getUserFromToken(orderRequest.getToken());
        String[] attributes = {"id", "orderStatus", "transactionId", "userId"};

        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", orderRequest.getOrderId())
                .withAttributesToGet(attributes);
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER).getItem(itemSpec);
        GetOrderResponse orderResponse = new GetOrderResponse();
        if(Objects.nonNull(item) && item.get("userId").equals(userId)){
            try {
                orderResponse = new ObjectMapper().readValue(item.toJSON(), GetOrderResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return orderResponse;
    }
}
