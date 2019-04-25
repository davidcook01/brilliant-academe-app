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
import com.brilliant.academe.domain.video.PreviewCourseVideoRequest;
import com.brilliant.academe.util.CommonUtils;

import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class GetPreviewCourseVideoHandler implements RequestHandler<PreviewCourseVideoRequest, CommonResponse> {

    private DynamoDB dynamoDB;

    @Override
    public CommonResponse handleRequest(PreviewCourseVideoRequest previewCourseVideoRequest, Context context) {
        initDynamoDbClient();
        return execute(previewCourseVideoRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public CommonResponse execute(PreviewCourseVideoRequest previewCourseVideoRequest){
        CommonResponse previewCourseVideoResponse = new CommonResponse();
        String lectureLink = getLectureLink(previewCourseVideoRequest.getCourseId(), previewCourseVideoRequest.getLectureId());
        if(Objects.nonNull(lectureLink)) {
            String signedUrl = CommonUtils.getSignedUrlForObject(lectureLink, dynamoDB);
            previewCourseVideoResponse.setSignedUrl(signedUrl);
            previewCourseVideoResponse.setMessage(STATUS_SUCCESS);
        }else{
            previewCourseVideoResponse.setMessage(NOT_AVAILABLE);
        }
        return previewCourseVideoResponse;
    }

    private String getLectureLink(String courseId, String lectureId){
        GetCourseLectureResponse response = CommonUtils.getCourseLectures(courseId, dynamoDB);
        for(CourseSection section: response.getCourseSection()){
            for(CourseLecture lecture: section.getLectures()){
                if(lecture.getLectureId().equals(lectureId))
                    if(lecture.isPreviewAvailable())
                        return lecture.getLectureLink();
            }
        }
        return null;
    }
}
