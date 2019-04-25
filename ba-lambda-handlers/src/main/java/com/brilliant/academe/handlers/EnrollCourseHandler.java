package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.common.CommonResponse;
import com.brilliant.academe.domain.enrollment.EnrollCourseRequest;
import com.brilliant.academe.util.CommonUtils;

import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class EnrollCourseHandler implements RequestHandler<EnrollCourseRequest, CommonResponse> {

    private DynamoDB dynamoDB;

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    @Override
    public CommonResponse handleRequest(EnrollCourseRequest enrollCourseRequest, Context context) {
        initDynamoDbClient();
        return execute(enrollCourseRequest);
    }

    public CommonResponse execute(EnrollCourseRequest enrollCourseRequest){
        String userId = CommonUtils.getUserFromToken(enrollCourseRequest.getToken());
        CommonResponse response = new CommonResponse();
        response.setMessage(STATUS_FAILED);
        String courseId = null;
        if(Objects.nonNull(enrollCourseRequest.getBody())
                && Objects.nonNull(enrollCourseRequest.getBody().getCourseId())) {
            courseId = enrollCourseRequest.getBody().getCourseId();
            System.out.println(courseId);
            Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem("id", courseId);
            if(Objects.nonNull(item)){
                if(item.get("courseType").equals("Free")){
                    CommonUtils.enrollUserCourse(userId, courseId, dynamoDB);
                    response.setMessage(STATUS_SUCCESS);
                }
            }
        }
        return response;
    }
}
