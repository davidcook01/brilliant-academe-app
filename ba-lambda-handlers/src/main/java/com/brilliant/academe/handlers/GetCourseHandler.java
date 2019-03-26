package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.GetCourseRequest;
import com.brilliant.academe.domain.course.GetCourseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GetCourseHandler implements RequestHandler<GetCourseRequest, GetCourseResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_COURSE = "ba_course";
    private Regions REGION = Regions.US_EAST_1;

    @Override
    public GetCourseResponse handleRequest(GetCourseRequest courseRequest, Context context) {
        this.initDynamoDbClient();
        return getData(courseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private GetCourseResponse getData(GetCourseRequest courseRequest){
        Table table = dynamoDb.getTable(DYNAMODB_TABLE_NAME_COURSE);
        Index index = table.getIndex("id-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("id = :v_course_id")
                .withValueMap(new ValueMap()
                        .withString(":v_course_id",courseRequest.getCourseId()));

        ItemCollection<QueryOutcome> items = index.query(querySpec);
        Iterator<Item> iter = items.iterator();

        List<GetCourseResponse> courses = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        while (iter.hasNext()) {
            try {
                courses.add(objectMapper.readValue(iter.next().toJSON(), GetCourseResponse.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return courses.stream().limit(1).findAny().get();
    }
}
