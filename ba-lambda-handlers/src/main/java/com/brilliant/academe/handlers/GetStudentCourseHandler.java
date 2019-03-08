package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.GetStudentCourseRequest;
import com.brilliant.academe.domain.course.GetStudentCourseResponse;
import com.brilliant.academe.domain.enrollment.EnrollCourseInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetStudentCourseHandler implements RequestHandler<GetStudentCourseRequest, GetStudentCourseResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_USER_COURSE = "ba_user_course";
    private Regions REGION = Regions.US_EAST_1;

    public GetStudentCourseResponse handleRequest(GetStudentCourseRequest studentCourseRequest, Context context) {
        this.initDynamoDbClient();
        return getData(studentCourseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private GetStudentCourseResponse getData(GetStudentCourseRequest getStudentCourseRequest){

        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_USER_COURSE);
        Index index = table.getIndex("userId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("userId = :v_user_id")
                .withValueMap(new ValueMap()
                        .withString(":v_user_id",getStudentCourseRequest.getUserId()));

        ItemCollection<QueryOutcome> items = index.query(querySpec);
        Iterator<Item> iter = items.iterator();

        List<EnrollCourseInfo> enrollCourseInfos = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        while (iter.hasNext()) {
            try {
                enrollCourseInfos.add(objectMapper.readValue(iter.next().toJSON(), EnrollCourseInfo.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        GetStudentCourseResponse studentCourseResponse = new GetStudentCourseResponse();
        studentCourseResponse.setCourses(enrollCourseInfos);
        return studentCourseResponse;
    }
}
