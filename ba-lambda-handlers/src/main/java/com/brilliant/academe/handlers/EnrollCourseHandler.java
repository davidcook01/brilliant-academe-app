package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.enrollment.EnrollCourseInfo;
import com.brilliant.academe.domain.enrollment.EnrollCourseRequest;
import com.brilliant.academe.domain.enrollment.EnrollCourseResponse;

public class EnrollCourseHandler implements RequestHandler<EnrollCourseRequest, EnrollCourseResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_USER_COURSE = "ba_user_course";
    private Regions REGION = Regions.US_EAST_1;

    public EnrollCourseResponse handleRequest(EnrollCourseRequest enrollCourseRequest, Context context) {
        this.initDynamoDbClient();
        persistData(enrollCourseRequest);
        EnrollCourseResponse response = new EnrollCourseResponse();
        response.setMessage("SUCCESS");
        return response;
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDb = new DynamoDB(client);
    }

    private void persistData(EnrollCourseRequest enrollCourseRequest)
            throws ConditionalCheckFailedException {

        Table courseTable = this.dynamoDb.getTable(DYNAMODB_TABLE_NAME_USER_COURSE);
        for(EnrollCourseInfo enrollCourseInfo: enrollCourseRequest.getBody().getCourses()){
            courseTable.putItem(new PutItemSpec().withItem(new Item()
                    .withString("userId", enrollCourseRequest.getUserId())
                    .withString("courseId", enrollCourseInfo.getCourseId())
                    .withString("courseName", enrollCourseInfo.getCourseName())
                    .withString("description", enrollCourseInfo.getCourseDescription())
                    .withString("coverImage", enrollCourseInfo.getCoverImage())
                    .withString("instructorId", enrollCourseInfo.getInstructorId())
                    .withString("instructorName", enrollCourseInfo.getInstructorName())
                    .withNumber("percentageCompleted", enrollCourseInfo.getPercentageCompleted())
                    .withNumber("courseDuration", enrollCourseInfo.getCourseDuration())));
        }
    }
}
