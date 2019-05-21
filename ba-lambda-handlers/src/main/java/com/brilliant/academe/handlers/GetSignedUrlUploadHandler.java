package com.brilliant.academe.handlers;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.brilliant.academe.constant.Constant;
import com.brilliant.academe.domain.common.CommonResponse;
import com.brilliant.academe.util.CommonUtils;
import com.google.gson.Gson;

import java.net.URL;
import java.util.Objects;

import static com.brilliant.academe.constant.Constant.REGION;

public class GetSignedUrlUploadHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private DynamoDB dynamoDB;
    private Item itemConfig;
    private String[] attributes = {"s3ContentUploadFolder", "s3ImageUploadFolder"};

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        initDynamoDbClient();
        initConfig();
        return execute(requestEvent);
    }

    private void initDynamoDbClient() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withRegion(REGION)
                .build();
        dynamoDB = new DynamoDB(client);
    }

    private void initConfig(){
        itemConfig = CommonUtils.getConfigInfo(dynamoDB, attributes);
    }

    public APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent requestEvent){
        String token = requestEvent.getHeaders().get(Constant.HEADER_AUTHORIZATION);
        String key = requestEvent.getQueryStringParameters().get("key");
        String name = requestEvent.getQueryStringParameters().get("name");
        String type = requestEvent.getQueryStringParameters().get("type");

        System.out.println(token+", "+ key+", "+ name+", "+type);

        String userId = CommonUtils.getUserFromToken(token);
        Item item = dynamoDB.getTable(Constant.DYNAMODB_TABLE_NAME_USER).getItem("id", userId);

        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setMessage(Constant.STATUS_FAILED);

        if(Objects.nonNull(item)){
            if(Objects.nonNull(item.get("instructor"))){
                boolean isInstructor = (Boolean) item.get("instructor");
                if(name.equals(Constant.S3_PROFILE_FOLDER)){
                    key = userId+key;
                    isInstructor = true;
                }
                if(isInstructor){
                    String bucketName = null;
                    String originPath = null;

                    if(type.equals(Constant.CF_VIDEOS_ORIGIN_PATH)){
                        bucketName = (String) itemConfig.get("s3ContentUploadFolder");
                        originPath = Constant.CF_VIDEOS_ORIGIN_PATH;
                    }else if(type.equals(Constant.CF_IMAGES_ORIGIN_PATH)){
                        bucketName = (String) itemConfig.get("s3ImageUploadFolder");
                        originPath = Constant.CF_IMAGES_ORIGIN_PATH;
                    }

                    if(Objects.nonNull(bucketName) && Objects.nonNull(originPath)){
                        String signedUrl = getSignedUrl(bucketName, originPath, name, key);
                        if(Objects.nonNull(signedUrl)){
                            commonResponse.setMessage(Constant.STATUS_SUCCESS);
                            commonResponse.setSignedUrl(signedUrl);
                        }
                    }
                }
            }
        }

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setBody(new Gson().toJson(commonResponse));
        return CommonUtils.setCorsHeaders(responseEvent);
    }

    private String getSignedUrl(String bucketName, String originPath, String name, String key){
        String clientRegion = Constant.REGION.US_EAST_1.getName();
        String objectKey = originPath + "/" + name + "/" + key;
        System.out.println("Key:"+objectKey);
        String signedUrl = null;
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();

            java.util.Date expiration = new java.util.Date();
            long expTimeMillis = expiration.getTime();
            expTimeMillis += 1000 * 60 * 60;
            expiration.setTime(expTimeMillis);

            System.out.println("Generating pre-signed URL.");
            GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
                    .withMethod(HttpMethod.PUT)
                    .withExpiration(expiration);
            URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
            signedUrl = url.toString();
        }
        catch(AmazonServiceException e) {
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            e.printStackTrace();
        }
        System.out.println("Url:"+signedUrl);
        return signedUrl;
    }
}
