package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.brilliant.academe.constant.Constant;
import com.brilliant.academe.domain.order.GetOrderResponse;
import com.brilliant.academe.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.DYNAMODB_TABLE_NAME_ORDER;
import static com.brilliant.academe.constant.Constant.REGION;

public class GetOrderHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDB dynamoDB;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        initDynamoDbClient();
        String token = requestEvent.getHeaders().get(Constant.HEADER_AUTHORIZATION);
        String orderId = requestEvent.getPathParameters().get("orderId");
        GetOrderResponse orderResponse = execute(token, orderId);
        return CommonUtils.setResponseBodyAndCorsHeaders(orderResponse);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public GetOrderResponse execute(String token, String orderId){
        String userId = CommonUtils.getUserFromToken(token);
        String[] attributes = {"id", "orderStatus", "transactionId", "userId"};
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", orderId)
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
