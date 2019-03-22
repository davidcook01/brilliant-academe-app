package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.lookup.LookupMasterResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class LookupMasterHandler implements RequestHandler<Void, LookupMasterResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_LOOKUP = "ba_lookup";
    private String LOOKUP_ID = "1";
    private Regions REGION = Regions.US_EAST_1;

    @Override
    public LookupMasterResponse handleRequest(Void request, Context context) {
        this.initDynamoDbClient();
        return getData();
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private LookupMasterResponse getData() {
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_LOOKUP);
        Item item = table.getItem("id", LOOKUP_ID);
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
