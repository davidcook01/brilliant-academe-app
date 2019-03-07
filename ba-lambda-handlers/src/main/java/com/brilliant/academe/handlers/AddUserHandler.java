package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.CognitoPostConfirmationRequest;

public class AddUserHandler implements RequestHandler<CognitoPostConfirmationRequest, String> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_USER = "ba_user";
    private Regions REGION = Regions.US_EAST_1;

    public String handleRequest(CognitoPostConfirmationRequest cognitoPostConfirmationRequest, Context context) {
        System.out.println("CognitoPostConfirmation Request - Start");
        System.out.println("User Id: "+ cognitoPostConfirmationRequest.getUserName());
        System.out.println("Email:"+cognitoPostConfirmationRequest.getRequest().getUserAttributes().get("email"));
        this.initDynamoDbClient();
        persistData(cognitoPostConfirmationRequest);
        System.out.println("CognitoPostConfirmation Request - End");
        return "SUCCESS";
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private PutItemOutcome persistData(CognitoPostConfirmationRequest cognitoPostConfirmationRequest)
            throws ConditionalCheckFailedException {
        return this.dynamoDb.getTable(DYNAMODB_TABLE_NAME_USER)
                .putItem(
                        new PutItemSpec().withItem(new Item()
                                .withString("userId", cognitoPostConfirmationRequest.getUserName())
                                .withString("email", cognitoPostConfirmationRequest.getRequest().getUserAttributes().get("email"))));
    }

}
