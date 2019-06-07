package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.lookup.LookupMasterResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;

import static com.brilliant.academe.constant.Constant.*;

public class LookupMasterHandler implements RequestHandler<Void, LookupMasterResponse> {

    private DynamoDB dynamoDB;

    @Override
    public LookupMasterResponse handleRequest(Void request, Context context) {
        System.out.println(new Gson().toJson(context));
        this.initDynamoDbClient();
        return execute();
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public LookupMasterResponse execute() {
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_LOOKUP).getItem("id", LOOKUP_ID);
        LookupMasterResponse response = new LookupMasterResponse();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            response = objectMapper.readValue(item.toJSON(), LookupMasterResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
