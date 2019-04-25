package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.common.CommonResponse;
import com.brilliant.academe.domain.course.CourseLecture;
import com.brilliant.academe.domain.course.CourseSection;
import com.brilliant.academe.domain.course.GetCourseLectureResponse;
import com.brilliant.academe.domain.video.CourseVideoRequest;
import com.brilliant.academe.util.CommonUtils;

import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class GetCourseVideoHandler implements RequestHandler<CourseVideoRequest, CommonResponse> {

    private DynamoDB dynamoDB;

    @Override
    public CommonResponse handleRequest(CourseVideoRequest courseVideoRequest, Context context) {
        initDynamoDbClient();
        return execute(courseVideoRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .withRegion(REGION)
            .build();
        dynamoDB = new DynamoDB(client);
    }

    public CommonResponse execute(CourseVideoRequest courseVideoRequest){
        String userId = CommonUtils.getUserFromToken(courseVideoRequest.getToken());
        CommonResponse courseVideoResponse = new CommonResponse();
        boolean isCourseExist = CommonUtils.checkIfCourseExistforUser(userId, courseVideoRequest.getCourseId(), dynamoDB);
        String lectureLink;
        if(isCourseExist){
            lectureLink = getLectureLink(courseVideoRequest.getCourseId(), courseVideoRequest.getLectureId());
            if(Objects.nonNull(lectureLink)){
                String signedUrl = CommonUtils.getSignedUrlForObject(lectureLink, dynamoDB);
                courseVideoResponse.setSignedUrl(signedUrl);
                courseVideoResponse.setMessage(STATUS_SUCCESS);
            }
        }else{
            courseVideoResponse.setMessage(NOT_AVAILABLE);
        }
        return courseVideoResponse;
    }

    private String getLectureLink(String courseId, String lectureId){
        GetCourseLectureResponse response = CommonUtils.getCourseLectures(courseId, dynamoDB);
        for(CourseSection section: response.getCourseSection()){
            for(CourseLecture lecture: section.getLectures()){
                if(lecture.getLectureId().equals(lectureId))
                    return lecture.getLectureLink();
            }
        }
        return null;
    }
}
