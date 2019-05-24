package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.brilliant.academe.domain.common.CommonResponse;
import com.brilliant.academe.domain.user.Instructor;
import com.brilliant.academe.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import java.io.IOException;

import static com.brilliant.academe.constant.Constant.*;

public class UpdateProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDB dynamoDB;
    private Item item;
    private String[] attributes = {"cfDistributionName"};

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        initDynamoDbClient();
        initConfig();
        String token = event.getHeaders().get(HEADER_AUTHORIZATION);
        CommonResponse commonResponse = execute(token, event.getBody());
        return CommonUtils.setResponseBodyAndCorsHeaders(commonResponse);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    private void initConfig(){
        item = CommonUtils.getConfigInfo(dynamoDB, attributes);
    }

    private CommonResponse execute(String token, String requestBody){
        String userId = CommonUtils.getUserFromToken(token);
        Instructor instructorMapperDetails = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            instructorMapperDetails = objectMapper.readValue(requestBody, Instructor.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String cfDistributionName = (String) item.get("cfDistributionName");
        String CF_IMAGE_URL = "https://" + cfDistributionName + "/" + CF_IMAGES_ORIGIN_PATH +"/profile/" + userId+instructorMapperDetails.getProfileImage();

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setMessage(STATUS_FAILED);
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("id", userId)
                .withUpdateExpression("set profileImage=:p, instructorDetails=:d")
                .withValueMap(new ValueMap()
                        .withString(":p", CF_IMAGE_URL)
                        .withJSON(":d", getInstructorDetails(instructorMapperDetails)));
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER).updateItem(updateItemSpec);
        commonResponse.setMessage(STATUS_SUCCESS);
        return commonResponse;
    }

    private String getInstructorDetails(Instructor instructorMapperDetails){
        Instructor instructorDetails = new Instructor();
        instructorDetails.setDesignation(instructorMapperDetails.getDesignation());
        instructorDetails.setDetailedDescription(instructorMapperDetails.getDetailedDescription());
        return CommonUtils.convertObjectToJson(instructorDetails);
    }
}
