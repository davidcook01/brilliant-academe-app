package com.brilliant.academe.util;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.google.gson.Gson;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Map;

import static com.brilliant.academe.constant.Constant.CLOUDFRONT_PRIVATE_KEY;

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

}
