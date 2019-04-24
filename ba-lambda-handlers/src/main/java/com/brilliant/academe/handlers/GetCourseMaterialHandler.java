package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.*;
import com.brilliant.academe.util.CommonUtils;

import java.util.Objects;

import static com.brilliant.academe.constant.Constant.*;

public class GetCourseMaterialHandler implements RequestHandler<CourseMaterialRequest, CourseMaterialResponse> {

    private DynamoDB dynamoDB;

    @Override
    public CourseMaterialResponse handleRequest(CourseMaterialRequest courseMaterialRequest, Context context) {
        initDynamoDbClient();
        return execute(courseMaterialRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    public CourseMaterialResponse execute(CourseMaterialRequest courseMaterialRequest){
        String userId = CommonUtils.getUserFromToken(courseMaterialRequest.getToken());
        boolean isCourseExist = CommonUtils.checkIfCourseExistforUser(userId, courseMaterialRequest.getCourseId(), dynamoDB);
        CourseMaterialResponse courseMaterialResponse = new CourseMaterialResponse();
        courseMaterialResponse.setMessage(NOT_AVAILABLE);
        if(isCourseExist){
            String materialLink = getMaterialLink(courseMaterialRequest);
            if(Objects.nonNull(materialLink)){
                courseMaterialResponse.setMessage(STATUS_SUCCESS);
                courseMaterialResponse.setSignedUrl(CommonUtils.getSignedUrlForObject(materialLink, dynamoDB));
            }
        }
        return courseMaterialResponse;
    }

    private String getMaterialLink(CourseMaterialRequest courseMaterialRequest){
        GetCourseLectureResponse response = CommonUtils.getCourseLectures(courseMaterialRequest.getCourseId(), dynamoDB);
        String materialLink = null;
        if(Objects.nonNull(response.getCourseSection()) && response.getCourseSection().size() > 0){
            for(CourseSection section: response.getCourseSection()){
                if(Objects.nonNull(section.getLectures()) && section.getLectures().size() > 0){
                    for(CourseLecture lecture: section.getLectures()){
                        if(Objects.nonNull(lecture.getMaterials()) && lecture.getMaterials().size() > 0){
                            for(CourseMaterial material: lecture.getMaterials()){
                                if(lecture.getLectureId().equals(courseMaterialRequest.getLectureId())
                                        && material.getMaterialId().equals(courseMaterialRequest.getMaterialId())){
                                    materialLink = material.getMaterialLink();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return materialLink;
    }
}
