package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.BatchGetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.enrollment.EnrollCourseInfo;
import com.brilliant.academe.domain.enrollment.GetEnrolledCourseRequest;
import com.brilliant.academe.domain.enrollment.GetEnrolledCourseResponse;
import com.brilliant.academe.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.DYNAMODB_TABLE_NAME_COURSE_RESOURCE;
import static com.brilliant.academe.constant.Constant.REGION;

public class GetEnrolledCourseHandler implements RequestHandler<GetEnrolledCourseRequest, GetEnrolledCourseResponse> {

    private DynamoDB dynamoDB;

    @Override
    public GetEnrolledCourseResponse handleRequest(GetEnrolledCourseRequest enrolledCourseRequest, Context context) {
        this.initDynamoDbClient();
        return execute(enrolledCourseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDB = new DynamoDB(client);
    }

    public GetEnrolledCourseResponse execute(GetEnrolledCourseRequest enrolledCourseRequest){
        String userId = CommonUtils.getUserFromToken(enrolledCourseRequest.getToken());
        ItemCollection<QueryOutcome> userCourseItems = CommonUtils.getUserEnrolledCourses(userId, dynamoDB);

        List<String> enrolledCourses = new ArrayList();
        for(Item item: userCourseItems){
            enrolledCourses.add((String) item.get("courseId"));
        }

        List<EnrollCourseInfo> enrollCourseInfos = new ArrayList<>();

        String[] attributes = {"id", "courseDuration", "courseLevel", "courseName", "courseType",
                "coverImage", "description", "discountedPrice",
                "instructorId", "instructorName", "price"};

        if(Objects.nonNull(enrolledCourses) && enrolledCourses.size() > 0){
            BatchGetItemOutcome batchGetItemOutcome = dynamoDB.batchGetItem(new BatchGetItemSpec()
                    .withTableKeyAndAttributes(new TableKeysAndAttributes(DYNAMODB_TABLE_NAME_COURSE_RESOURCE)
                            .withHashOnlyKeys("id", enrolledCourses.toArray())
                            .withAttributeNames(attributes)
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

        GetEnrolledCourseResponse enrolledCourseResponse = new GetEnrolledCourseResponse();
        enrolledCourseResponse.setCourses(enrollCourseInfos);
        return enrolledCourseResponse;
    }
}
