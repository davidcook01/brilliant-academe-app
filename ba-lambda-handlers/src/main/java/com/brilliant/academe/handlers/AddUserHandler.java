package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.user.CognitoPostConfirmationRequest;
import com.brilliant.academe.util.CommonUtils;

import static com.brilliant.academe.constant.Constant.*;

public class AddUserHandler implements RequestHandler<CognitoPostConfirmationRequest, String> {

    private DynamoDB dynamoDB;

    @Override
    public String handleRequest(CognitoPostConfirmationRequest cognitoPostConfirmationRequest, Context context) {
        initDynamoDbClient();
        return execute(cognitoPostConfirmationRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public String execute(CognitoPostConfirmationRequest cognitoPostConfirmationRequest){
        System.out.println(CommonUtils.convertObjectToJson(cognitoPostConfirmationRequest));
        persistData(cognitoPostConfirmationRequest);
        return STATUS_SUCCESS;
    }

    private void persistData(CognitoPostConfirmationRequest cognitoPostConfirmationRequest)
            throws ConditionalCheckFailedException {
        boolean isInstructor = false;
        if(cognitoPostConfirmationRequest.getRequest().getUserAttributes().get("custom:instructor").equals("Y")){
            isInstructor = true;
        }
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER)
            .putItem(new PutItemSpec().withItem(new Item()
                    .withString("id", cognitoPostConfirmationRequest.getUserName())
                    .withString("email", cognitoPostConfirmationRequest.getRequest().getUserAttributes().get("email"))
                    .withString("fullName", cognitoPostConfirmationRequest.getRequest().getUserAttributes().get("name"))
                    .withBoolean("instructor", isInstructor)));
    }
}