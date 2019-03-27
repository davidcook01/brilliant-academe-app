package com.brilliant.academe.handlers;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.BatchGetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.GetStudentCourseRequest;
import com.brilliant.academe.domain.course.GetStudentCourseResponse;
import com.brilliant.academe.domain.enrollment.EnrollCourseInfo;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetStudentCourseHandler implements RequestHandler<GetStudentCourseRequest, GetStudentCourseResponse> {

    private DynamoDB dynamoDb;
    private String DYNAMODB_TABLE_NAME_USER_COURSE = "ba_user_course";
    private String DYNAMODB_TABLE_NAME_COURSE_RESOURCE = "ba_course_resource";
    private Regions REGION = Regions.US_EAST_1;

    @Override
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

        Table userCourseTable = dynamoDb.getTable(DYNAMODB_TABLE_NAME_USER_COURSE);
        Index index = userCourseTable.getIndex("userId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("userId = :v_user_id")
                .withValueMap(new ValueMap()
                        .withString(":v_user_id",getStudentCourseRequest.getUserId()));

        ItemCollection<QueryOutcome> userCourseItems = index.query(querySpec);
        List<String> enrolledCourses = new ArrayList();
        for(Item item: userCourseItems){
            enrolledCourses.add((String) item.get("courseId"));
        }

        List<EnrollCourseInfo> enrollCourseInfos = new ArrayList<>();

        if(Objects.nonNull(enrolledCourses) && enrolledCourses.size() > 0){
            BatchGetItemOutcome batchGetItemOutcome = dynamoDb.batchGetItem(new BatchGetItemSpec()
                    .withTableKeyAndAttributes(new TableKeysAndAttributes(DYNAMODB_TABLE_NAME_COURSE_RESOURCE)
                            .withHashOnlyKeys("id", enrolledCourses.toArray())
                            .withConsistentRead(true)));
            List<Item> courseItemsList = batchGetItemOutcome.getTableItems().get(DYNAMODB_TABLE_NAME_COURSE_RESOURCE);
            ObjectMapper objectMapper = new ObjectMapper();
            for(Item courseItem: courseItemsList){
                try {
                    enrollCourseInfos.add(objectMapper.readValue(courseItem.toJSON(), EnrollCourseInfo.class));
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        for(Item userCourseItem: userCourseItems){
            String courseId = (String)userCourseItem.get("courseId");
            for(EnrollCourseInfo enrollCourseInfo: enrollCourseInfos){
                if(courseId.equals(enrollCourseInfo.getCourseId())){
                    BigDecimal percentageCompleted = (BigDecimal) userCourseItem.get("percentageCompleted");
                    BigDecimal courseRating = (BigDecimal) userCourseItem.get("courseRating");
                    if(Objects.nonNull(percentageCompleted))
                        enrollCourseInfo.setPercentageCompleted(percentageCompleted.intValue());
                    if(Objects.nonNull(courseRating))
                        enrollCourseInfo.setCourseRating(courseRating.floatValue());
                    break;
                }
            }
        }

        GetStudentCourseResponse studentCourseResponse = new GetStudentCourseResponse();
        studentCourseResponse.setCourses(enrollCourseInfos);
        return studentCourseResponse;
    }
}
