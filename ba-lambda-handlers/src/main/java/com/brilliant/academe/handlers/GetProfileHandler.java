package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.brilliant.academe.domain.user.Instructor;
import com.brilliant.academe.util.CommonUtils;
import com.google.gson.Gson;

import java.util.Objects;

import static com.brilliant.academe.constant.Constant.HEADER_AUTHORIZATION;
import static com.brilliant.academe.constant.Constant.REGION;

public class GetProfileHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDB dynamoDB;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        initDynamoDbClient();
        String token = requestEvent.getHeaders().get(HEADER_AUTHORIZATION);
        Instructor instructor = execute(token);
        return CommonUtils.setResponseBodyAndCorsHeaders(instructor);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    private Instructor execute(String token) {
        String userId = CommonUtils.getUserFromToken(token);
        Instructor instructorDetails = CommonUtils.getInstructorDetails(userId, dynamoDB);
        if(Objects.isNull(instructorDetails))
            instructorDetails = new Instructor();
        return instructorDetails;
    }
}
