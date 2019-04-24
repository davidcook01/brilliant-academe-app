package com.brilliant.academe.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.course.GetCourseLectureRequest;
import com.brilliant.academe.domain.course.GetCourseLectureResponse;
import com.brilliant.academe.util.CommonUtils;

import static com.brilliant.academe.constant.Constant.REGION;

public class GetCourseLectureHandler  implements RequestHandler<GetCourseLectureRequest, GetCourseLectureResponse> {

    private DynamoDB dynamoDB;

    @Override
    public GetCourseLectureResponse handleRequest(GetCourseLectureRequest courseRequest, Context context) {
        this.initDynamoDbClient();
        return execute(courseRequest);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        this.dynamoDB = new DynamoDB(client);
    }

    public GetCourseLectureResponse execute(GetCourseLectureRequest courseRequest){
        return CommonUtils.getCourseLectures(courseRequest.getCourseId(), dynamoDB);
    }
}
