package com.brilliant.academe.handlers;

import com.amazonaws.services.cloudfront.CloudFrontUrlSigner;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.brilliant.academe.domain.security.GenerateSignedUrlRequest;
import com.brilliant.academe.domain.security.GenerateSignedUrlResponse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

public class GenerateSignedUrlHandler implements RequestHandler<GenerateSignedUrlRequest, GenerateSignedUrlResponse> {

    private static final String PK_CONTENTS = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEowIBAAKCAQEAxmyE8Az2yR6DPg3Ap5vxMBK7WEMnFud7xrsftFnU7KzqXiGB\n" +
            "lw0FovLynbJao6UvLELPbE9cWbINNuHV2dLOt8p1xtUKIkelO+xFX2MJ01JikHx0\n" +
            "YRvtPxXZTA8hWmPGBCV55QtePSxka+8DM6VujUugjN6i9li3HuVdtkts9hIyRMED\n" +
            "+BuWgQB1uhqp/STiWSFWaROugEAKzYmfwnbKXWFjcefQQ9ybUeikPe4dzK9oeVpG\n" +
            "voMI/KqK2U8pRCNf7sh/yb1O1COXtd83oNFL7lthcw/VDct5T+36QKC5ZB5OGzbQ\n" +
            "gAd72LAgGI0DYsRcoNHJgpPo0zv6M4XOKqnRDQIDAQABAoIBABmuPd5bry0Ed/P2\n" +
            "D7MhqauRIRIhTtPWDd0Apu/OmJIxJvwb1gvYsZwPWXszdCut21cdJn9cHhW1x6QM\n" +
            "woK4l+yNlYSwWelp4GbLA4d+5+yjwwH0OZO/64q4pzEC3Ew4kY5D02zjdcTxE+Ys\n" +
            "PvvWM2KyIQtaXTkI7lpfUSugw29tXM6tuxjg9J1TQaLMkqP6GdQdPPgRrZFx41WL\n" +
            "8cyjxp6TXmhHbLE87oroHZgsN/D1sUa3ypNs02oe9tJhiUFX+Kt/rWcoS1arm0DL\n" +
            "8mb6lukHGw5cx8sPHCUYMQo0X5zq7y4aZtXPW5N3D5DJI7kiuxwf0zmRMW1znnOc\n" +
            "sWQankECgYEA+ljx+tzYUdmS1Ywq1ySW6+zSylWsj+A8ZotEntSRt+Q203hpqIyd\n" +
            "GRWO2CS85BObjItRIMB1n3UQOAJTitI6hzdOu7yt99JTi6Ay/mQTBjJBU90CwUxR\n" +
            "DFOG9ZS18AUYmQsq/S4hSQU+KYNOwZRvzUi6OQYVpRNzaGgpTDOci2UCgYEAyudy\n" +
            "Q6PsXE7RrcJsfYlGf584mkgzdbH1X1dbqNeDTmc9H+QRpz47OsVLeeAIt+gci1Uq\n" +
            "AdvIwQcUDCygmjFnm9OItWr+e8yqJDe937DaGmfVmUhO7btasuFZfiupx/Ggt4Ga\n" +
            "ErHe6cMB1nl0cqGl3JVFpB9mkoDp9AdEc+/O2IkCgYEAlVkkmqzPXBz5TO4+zHRm\n" +
            "mL6CjfSIapiyT9Z7jGlxuQurbYLjPYsNWV6UBfv8t+++lxyxvGE0inkywMcagbGi\n" +
            "+vGSxcrs2fAeqShb44leFA89C8PIlfpqS3k1BSK/Wz4fC4YvEjVH5CD9kZRlEnT0\n" +
            "MbpWZhhsJzuwWLPzFyOfG0UCgYAnZmYDyHL/QbbpnNVgUyXDZnGhU9/aPOI+z0HX\n" +
            "OXIf6WAvLyRWa3ko4mLTmbNXstTIIZN0pO8IvI0iCBBvoKRT1/G1+L7N32Iygjh2\n" +
            "lWrkscECcM4Sz8y+649rNqNTVhI1UA9RDgURM3LJ6O+5yrXgbYQGaKqMTEghwL+E\n" +
            "G874sQKBgB4xBAmn0UWuudZiMxwaR3Iz4F5UEHEsHhTuves2xnoWjzFoZQgi3MYY\n" +
            "c2wmtrnxDBtmku4pBGjmVJtFvIyPE9XjUgRrc+0NY6X6oBYTqUCRZ0A/A/zxV+m6\n" +
            "Xrmo6tTJKUZ6ZwG/kYGEn1XtBl2wN7xDpzR8qYentOBNfSAewTD5\n" +
            "-----END RSA PRIVATE KEY-----\n";

    private static final String CLOUDFRONT_DISTRIBUTION_NAME = "dtq2zus45gezp.cloudfront.net";
    private static final String CLOUDFRONT_KEY_PAIR_ID = "APKAIVO4XJDY4FXBPMHA";
    private static final Integer EXPIRY_IN_SECONDS = 60 * 1000;

    @Override
    public GenerateSignedUrlResponse handleRequest(GenerateSignedUrlRequest generateSignedUrlRequest, Context context) {
        return execute(generateSignedUrlRequest);
    }

    private GenerateSignedUrlResponse execute(GenerateSignedUrlRequest generateSignedUrlRequest) {
        String s3ObjectKey = generateSignedUrlRequest.getObjectName();
        File cloudFrontPrivateKeyFile = null;
        BufferedWriter bw = null;
        try {
            cloudFrontPrivateKeyFile = File.createTempFile("rsa_pk",".pem");
            bw = new BufferedWriter(new FileWriter(cloudFrontPrivateKeyFile));
            bw.write(PK_CONTENTS);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        GenerateSignedUrlResponse generateSignedUrlResponse = new GenerateSignedUrlResponse();
        generateSignedUrlResponse.setSignedUrl(createSignedUrl(cloudFrontPrivateKeyFile, s3ObjectKey));
        return generateSignedUrlResponse;
    }

    private String createSignedUrl(File cloudFrontPrivateKeyFile, String s3ObjectKey){
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRY_IN_SECONDS);
        String signedUrl = null;
        try {
            signedUrl = CloudFrontUrlSigner.getSignedURLWithCannedPolicy(
                    CloudFrontUrlSigner.Protocol.https,
                    CLOUDFRONT_DISTRIBUTION_NAME,
                    cloudFrontPrivateKeyFile,
                    s3ObjectKey,
                    CLOUDFRONT_KEY_PAIR_ID,
                    expirationDate);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signedUrl;
    }
}
