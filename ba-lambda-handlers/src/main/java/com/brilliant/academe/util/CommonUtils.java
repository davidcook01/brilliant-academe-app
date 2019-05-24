package com.brilliant.academe.util;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.BatchGetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.brilliant.academe.constant.Constant;
import com.brilliant.academe.domain.course.GetCourseLectureResponse;
import com.brilliant.academe.domain.user.Instructor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.math.BigDecimal;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.brilliant.academe.constant.Constant.*;

/**
 * @author Karthik Sitaraman
 * This is a Utility Class or Helper class, which contains just static methods, it is stateless and cannot be instantiated.
 * It contains a bunch of related methods, so they can be reused across the application.
 */
public class CommonUtils {

    /**
     * Returns User Id based on the input token
     * @param encodedToken
     * @return
     */
    public static String getUserFromToken(String encodedToken){
        String[] pieces = encodedToken.split("\\.");
        if(Objects.nonNull(pieces) && pieces.length == 3){
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
        return null;
    }

    /**
     * Returns Cloudfront Signed URL retrieving videos and materials
     * @param s3ObjectKey
     * @param dynamoDB
     * @return
     */
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
        return generateSignedUrl(expirySeconds, cloudFrontDistributionName, s3ObjectKey, cloudFrontKeyPairId, cloudFrontPrivateKey);
    }

    /**
     * Generates Cloudfront Signed URL with configurable expiry time. Expiry Time can be changed in database.     * @param expirySeconds
     * @param cloudFrontDistributionName
     * @param s3ObjectKey
     * @param cloudFrontKeyPairId
     * @param cloudFrontPrivateKey
     * @return
     */
    private static String generateSignedUrl(Integer expirySeconds, String cloudFrontDistributionName,
                                           String s3ObjectKey, String cloudFrontKeyPairId, String cloudFrontPrivateKey){
        File cloudFrontPrivateKeyFile = null;
        BufferedWriter bw = null;
        try {
            cloudFrontPrivateKeyFile = File.createTempFile("rsa_pk",".pem");
            bw = new BufferedWriter(new FileWriter(cloudFrontPrivateKeyFile));
            bw.write(cloudFrontPrivateKey);
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

    /**
     * Returns true or false based on user's course purchases
     * @param userId
     * @param courseId
     * @param dynamoDB
     * @return
     */
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

    /**
     * Get Enrolled Courses for a User
     * @param userId
     * @param dynamoDB
     * @return
     */
    public static ItemCollection<QueryOutcome> getUserEnrolledCourses(String userId, DynamoDB dynamoDB){
        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER_COURSE).getIndex("userId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("userId = :v_user_id")
                .withValueMap(new ValueMap()
                        .withString(":v_user_id", userId));

        return index.query(querySpec);
    }

    /**
     * Get Lectures details for a given Course Id
     * @param courseId
     * @param dynamoDB
     * @return
     */
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


    /**
     * Returns list of courses for given Course Ids by making Batch Call
     * @param dynamoDB
     * @param courses
     * @param attributes
     * @return
     */
    public static List<Item> getCoursesList(DynamoDB dynamoDB, List<String> courses, List<String> attributes){
        BatchGetItemOutcome batchGetItemOutcome = dynamoDB.batchGetItem(new BatchGetItemSpec()
                .withTableKeyAndAttributes(new TableKeysAndAttributes(DYNAMODB_TABLE_NAME_COURSE_RESOURCE)
                        .withHashOnlyKeys("id", courses.toArray())
                        .withAttributeNames(attributes)
                        .withConsistentRead(true)));
        return batchGetItemOutcome.getTableItems().get(DYNAMODB_TABLE_NAME_COURSE_RESOURCE);
    }

    /**
     * Get Items from the Cart for a given User
     * @param userId
     * @param dynamoDB
     * @return
     */
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

    /**
     * Returns Date and Time for US/Eastern timezone
     * @return
     */
    public static String getDateTime(){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        return sdf.format(date);
    }

    /**
     * Enrolls Free Course for a User
     * @param userId
     * @param courseId
     * @param dynamoDB
     */
    public static void enrollUserCourse(String userId, String courseId, DynamoDB dynamoDB){
        PutItemSpec putItemSpec = new PutItemSpec();
        putItemSpec.withItem(new Item()
                .withString("userId", userId)
                .withString("courseId", courseId)
                .withNumber("percentageCompleted", 0));
        dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER_COURSE).putItem(putItemSpec);
    }

    /**
     * Returns list of Users for a given Course Id
     * @param courseId
     * @param dynamoDB
     * @return
     */
    public static ItemCollection<QueryOutcome> getUsersByCourseId(String courseId, DynamoDB dynamoDB){
        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER_COURSE).getIndex("courseId-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("courseId = :v_course_id")
                .withValueMap(new ValueMap()
                        .withString(":v_course_id", courseId));
        return index.query(querySpec);
    }

    /**
     * Retruns list of Courses for a given Course Id in ba_course table
     * @param courseId
     * @param dynamoDB
     * @return
     */
    public static ItemCollection<QueryOutcome> getCoursesByCourseIdInMaster(String courseId, DynamoDB dynamoDB){
        Index index = dynamoDB.getTable(DYNAMODB_TABLE_NAME_COURSE).getIndex("id-index");
        QuerySpec querySpec = new QuerySpec()
                .withKeyConditionExpression("id = :v_course_id")
                .withValueMap(new ValueMap()
                        .withString(":v_course_id", courseId));

        return index.query(querySpec);
    }

    /**
     * Get details from Config table for given set of attributes
     * @param dynamoDB
     * @param attributes
     * @return
     */
    public static Item getConfigInfo(DynamoDB dynamoDB, String[] attributes){
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", CONFIG_ID)
                .withAttributesToGet(attributes);
        return dynamoDB.getTable(Constant.DYNAMODB_TABLE_NAME_CONFIG).getItem(itemSpec);
    }

    /**
     * Returns Response Body and CORS Headers
     * @param responseBody
     * @return
     */
    public static APIGatewayProxyResponseEvent setResponseBodyAndCorsHeaders(Object responseBody){
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            responseEvent.setBody(objectMapper.writeValueAsString(responseBody));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return setCorsHeaders(responseEvent);
    }

    /**
     * Return CORS Headers
     * @param responseEvent
     * @return
     */
    public static APIGatewayProxyResponseEvent setCorsHeaders(APIGatewayProxyResponseEvent responseEvent){
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Origin", "*");
        responseEvent.setHeaders(headers);
        return responseEvent;
    }

    /**
     * Return Instructor Details
     * @param userId
     * @param dynamoDB
     * @return
     */
    public static Instructor getInstructorDetails(String userId, DynamoDB dynamoDB){
        String[] attributes = {"fullName", "profileImage", "instructorDetails"};
        GetItemSpec itemSpec = new GetItemSpec()
                .withPrimaryKey("id", userId)
                .withAttributesToGet(attributes);
        Item item = dynamoDB.getTable(DYNAMODB_TABLE_NAME_USER).getItem(itemSpec);
        Instructor instructor = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String json = convertObjectToJson(item.get("instructorDetails"));
        try {
            instructor = objectMapper.readValue(json, Instructor.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(Objects.nonNull(instructor)) {
            instructor.setInstructorName((String) item.get("fullName"));
            instructor.setProfileImage((String) item.get("profileImage"));
        }
        return instructor;
    }

    /**
     * Convert an Object to JSON
     * @param object
     * @return
     */
    public static String convertObjectToJson(Object object){
        return new Gson().toJson(object);
    }

}
