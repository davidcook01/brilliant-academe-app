package com.brilliant.academe.constant;

import com.amazonaws.regions.Regions;

public class Constant {

    public static final Regions REGION = Regions.US_EAST_1;

    public static final String DYNAMODB_TABLE_NAME_LOOKUP = "ba_lookup";
    public static final String DYNAMODB_TABLE_NAME_USER = "ba_user";
    public static final String DYNAMODB_TABLE_NAME_COURSE = "ba_course";
    public static final String DYNAMODB_TABLE_NAME_COURSE_RESOURCE = "ba_course_resource";
    public static final String DYNAMODB_TABLE_NAME_ORDER_CART = "ba_order_cart";
    public static final String DYNAMODB_TABLE_NAME_ORDER = "ba_order";
    public static final String DYNAMODB_TABLE_NAME_USER_COURSE = "ba_user_course";
    public static final String DYNAMODB_TABLE_NAME_CONFIG = "ba_config";

    public static final String STRIPE_SECRET_KEY = "sk_test_UTifZqEZPT8SyQBTrWCSwfAn00xySX7uI7";
    public static final String ELASTIC_TRANSCODER_PIPELINE_ID = "1551221588927-5zpuyx";

    public static final String CF_IMAGES_ORIGIN_PATH = "/images/";
    public static final String CF_VIDEOS_ORIGIN_PATH = "content";

    public static final String STATUS_IN_PROCESS = "INPROCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_SUCCESS = "SUCCESS";

    public static final String EVENT_MODIFY = "MODIFY";
    public static final String EVENT_INSERT = "INSERT";
    public static final String NOT_AVAILABLE = "NOT AVAILABLE";

    public static final String CONFIG_ID = "1";
    public static final String LOOKUP_ID = "1";

    public static final String CLOUDFRONT_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
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


}
