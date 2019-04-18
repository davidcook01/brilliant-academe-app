package com.brilliant.academe.util;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.brilliant.academe.constant.Constant;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.math.BigDecimal;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public static String generateSignedUrl(Integer expirySeconds, String cloudFrontDistributionName,
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

    public static String getSignedUrlForObject(String s3ObjectKey){
        Integer expirySeconds = 60000;
        String cloudFrontDistributionName = "dtq2zus45gezp.cloudfront.net";
        String cloudFrontKeyPairId = "APKAIVO4XJDY4FXBPMHA";

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
        return CommonUtils.generateSignedUrl(expirySeconds, cloudFrontDistributionName, s3ObjectKey, cloudFrontKeyPairId);
    }

}
