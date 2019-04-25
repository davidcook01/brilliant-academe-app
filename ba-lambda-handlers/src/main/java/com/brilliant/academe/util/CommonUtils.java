package com.brilliant.academe.util;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.BatchGetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.brilliant.academe.constant.Constant;
import com.brilliant.academe.domain.course.GetCourseLectureResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.math.BigDecimal;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.brilliant.academe.constant.Constant.*;

public class CommonUtils {

    public static String getUserFromToken(String encodedToken){
        String[] pieces = encodedToken.split("\\.");
        String b64payload = pieces[1];
        String jsonString = null;
        try {
            jsonString = new String(Base64.decodeBase64(b64payload), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new Gson().fromJson(jsonString, Map.class);
        String userId = (String) map.get("sub");
        return userId;
    }

    public static String getSignedUrlForObject(String s3ObjectKey, DynamoDB dynamoDB){
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
        return generateSignedUrl(expirySeconds, cloudFrontDistributionName, s3ObjectKey, cloudFrontKeyPairId);
    }

    private static String generateSignedUrl(Integer expirySeconds, String cloudFrontDistributionName,
                                           String s3ObjectKey, String cloudFrontKeyPairId){
        File cloudFrontPrivateKeyFile = null;
        BufferedWriter bw = null;
        try {
            cloudFrontPrivateKeyFile = File.createTempFile("rsa_pk",".pem");
            bw = new BufferedWriter(new FileWriter(cloudFrontPrivateKeyFile));
            bw.write(CLOUDFRONT_PRIVATE_KEY);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Date expirationDate = new Date(System.currentTimeMillis() + expirySeconds);
        String signedUrl = null;
        try {
            signedUrl = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                    CloudFrontUrlSigner.Protocol.https,
                    cloudFrontDistributionName,
                    cloudFrontPrivateKeyFile,
                    s3ObjectKey,
                    cloudFrontKeyPairId,
                    expirationDate);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signedUrl;
    }

    public static boolean checkIfCourseExistforUser(String userId, String courseId, DynamoDB dynamoDB){
        ItemCollection<QueryOutcome> userCourseItems = getUserEnrolledCourses(userId, dynamoDB);
        List<String> enrolledCourses = new ArrayList();
        for(Item item: userCourseItems){
            enrolledCourses.add((String) item.get("courseId"));
        }
        if(enrolledCourses.contains(courseId)){
            return true;
        }
        return false;
    }

    public static ItemCollection<QueryOutcome> getUserEnrolledCourses(String userId, DynamoDB dynamoDB){
        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER_COURSE).getIndex("userId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("userId = :v_user_id")
                .withValueMap(new ValueMap()
                        .withString(":v_user_id", userId));

        return index.query(querySpec);
    }

    public static GetCourseLectureResponse getCourseLectures(String courseId, DynamoDB dynamoDB){
        GetCourseLectureResponse response = new GetCourseLectureResponse();
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", courseId)
                .withAttributesToGet("id", "resources");
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE_RESOURCE).getItem(itemSpec);
        if(Objects.nonNull(item)){
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                response = objectMapper.readValue(item.toJSON(), GetCourseLectureResponse.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    public static List<Item> getCoursesList(DynamoDB dynamoDB, List<String> courses, List<String> attributes){
        BatchGetItemOutcome batchGetItemOutcome = dynamoDB.batchGetItem(new BatchGetItemSpec()
                .withTableKeyAndAttributes(new TableKeysAndAttributes(DYNAMODB_TABLE_NAME_COURSE_RESOURCE)
                        .withHashOnlyKeys("id", courses.toArray())
                        .withAttributeNames(attributes)
                        .withConsistentRead(true)));
        return batchGetItemOutcome.getTableItems().get(DYNAMODB_TABLE_NAME_COURSE_RESOURCE);
    }

    public static ItemCollection<QueryOutcome> getUserInprocessCart(String userId, DynamoDB dynamoDB){
        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_ORDER_CART).getIndex("userId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("userId = :v_user_id")
                .withFilterExpression("cartStatus = :v_cart_status")
                .withValueMap(new ValueMap()
                        .withString(":v_user_id", userId)
                        .withString(":v_cart_status", STATUS_IN_PROCESS));
        return  index.query(querySpec);
    }

    public static String getDateTime(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        return sdf.format(date);
    }

    public static void enrollUserCourse(String userId, String courseId, DynamoDB dynamoDB){
        PutItemSpec putItemSpec = new PutItemSpec();
        putItemSpec.withItem(new Item()
                .withString("userId", userId)
                .withString("courseId", courseId)
                .withNumber("percentageCompleted", 0));
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER_COURSE).putItem(putItemSpec);
    }
}
