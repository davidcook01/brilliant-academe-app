package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.constant.Constant;
import com.brilliant.academe.domain.course.CourseLecture;
import com.brilliant.academe.domain.course.CourseSection;
import com.brilliant.academe.domain.course.GetCourseLectureResponse;
import com.brilliant.academe.domain.video.CourseVideoRequest;
import com.brilliant.academe.domain.video.CourseVideoResponse;
import com.brilliant.academe.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class GetCourseVideoHandler implements RequestHandler<CourseVideoRequest, CourseVideoResponse> {

    private DynamoDB dynamoDB;

    @Override
    public CourseVideoResponse handleRequest(CourseVideoRequest courseVideoRequest, Context context) {
        initDynamoDbClient();
        return execute(courseVideoRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(REGION)
            .build();
        dynamoDB = new DynamoDB(client);
    }

    public CourseVideoResponse execute(CourseVideoRequest courseVideoRequest){
        String userId = CommonUtils.getUserFromToken(courseVideoRequest.getToken());
        CourseVideoResponse courseVideoResponse = new CourseVideoResponse();
        courseVideoResponse.setSignedUrl("");
        boolean isCourseExist = checkIfCourseExistforUser(userId, courseVideoRequest.getCourseId());
        String lectureLink;
        if(isCourseExist){
            lectureLink = getLectureLink(courseVideoRequest.getCourseId(), courseVideoRequest.getLectureId());
            if(Objects.nonNull(lectureLink)){
                String signedUrl = getSignedUrl(lectureLink);
                courseVideoResponse.setSignedUrl(signedUrl);
                courseVideoResponse.setMessage(STATUS_SUCCESS);
            }
        }else{
            courseVideoResponse.setMessage(VIDEO_NOT_AVAILABLE);
        }
        return courseVideoResponse;
    }

    private boolean checkIfCourseExistforUser(String userId, String courseId){
        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER_COURSE).getIndex("userId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("userId = :v_user_id")
                .withValueMap(new ValueMap()
                        .withString(":v_user_id", userId));

        ItemCollection<QueryOutcome> userCourseItems = index.query(querySpec);
        List<String> enrolledCourses = new ArrayList();
        for(Item item: userCourseItems){
            enrolledCourses.add((String) item.get("courseId"));
        }

        if(enrolledCourses.contains(courseId)){
            return true;
        }
        return false;
    }

    private String getLectureLink(String courseId, String lectureId){
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", courseId)
                .withAttributesToGet("id", "resources");
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem(itemSpec);
        GetCourseLectureResponse response = new GetCourseLectureResponse();
        if(Objects.nonNull(item)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                response = objectMapper.readValue(item.toJSON(), GetCourseLectureResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(CourseSection section: response.getCourseSection()){
            for(CourseLecture lecture: section.getLectures()){
                if(lecture.getLectureId().equals(lectureId))
                    return lecture.getLectureLink();
            }
        }
        return null;
    }

    public String getSignedUrl(String s3ObjectKey) {
        String[] attributes = {"cfDistributionName", "cfExpirySeconds", "cfKeyPairId", "cfPrivateKey"};
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", CONFIG_ID)
                .withAttributesToGet(attributes);
        Item item = dynamoDB.getTable(Constant.DYNAMODB_TABLE_NAME_CONFIG).getItem(itemSpec);
        String cloudFrontDistributionName = (String) item.get("cfDistributionName");
        BigDecimal expirySecondsInBD = (BigDecimal) item.get("cfExpirySeconds");
        Integer expirySeconds = expirySecondsInBD.intValue();
        String cloudFrontKeyPairId = (String) item.get("cfKeyPairId");
        String cloudFrontPrivateKey = (String) item.get("cfPrivateKey");
        return CommonUtils.generateSignedUrl(expirySeconds, cloudFrontDistributionName, s3ObjectKey, cloudFrontKeyPairId);
    }
}
