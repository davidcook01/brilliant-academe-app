package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.constant.Constant;
import com.brilliant.academe.domain.course.CourseLecture;
import com.brilliant.academe.domain.course.CourseSection;
import com.brilliant.academe.domain.course.GetCourseLectureResponse;
import com.brilliant.academe.domain.video.PreviewCourseVideoRequest;
import com.brilliant.academe.domain.video.PreviewCourseVideoResponse;
import com.brilliant.academe.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class GetPreviewCourseVideoHandler implements RequestHandler<PreviewCourseVideoRequest, PreviewCourseVideoResponse> {

    private DynamoDB dynamoDB;

    @Override
    public PreviewCourseVideoResponse handleRequest(PreviewCourseVideoRequest previewCourseVideoRequest, Context context) {
        initDynamoDbClient();
        return execute(previewCourseVideoRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public PreviewCourseVideoResponse execute(PreviewCourseVideoRequest previewCourseVideoRequest){
        PreviewCourseVideoResponse previewCourseVideoResponse = new PreviewCourseVideoResponse();
        String lectureLink = getLectureLink(previewCourseVideoRequest.getCourseId(), previewCourseVideoRequest.getLectureId());
        previewCourseVideoResponse.setSignedUrl("");
        if(Objects.nonNull(lectureLink)) {
            String signedUrl = getSignedUrl(lectureLink);
            previewCourseVideoResponse.setSignedUrl(signedUrl);
            previewCourseVideoResponse.setMessage(STATUS_SUCCESS);
        }else{
            previewCourseVideoResponse.setMessage(VIDEO_NOT_AVAILABLE);
        }
        return previewCourseVideoResponse;
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
                    if(lecture.isPreviewAvailable())
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
        String signedUrl = CommonUtils.generateSignedUrl(expirySeconds, cloudFrontDistributionName, s3ObjectKey, cloudFrontKeyPairId);
        System.out.println(signedUrl);
        return signedUrl;
    }
}
