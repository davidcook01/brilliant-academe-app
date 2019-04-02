package com.brilliant.academe.constant;

import com.amazonaws.regions.Regions;

public class Constant {

    public static final Regions REGION = Regions.US_EAST_1;

    public static final String DYNAMODB_TABLE_NAME_LOOKUP = "ba_lookup";
    public static final String DYNAMODB_TABLE_NAME_USER = "ba_user";
    public static final String DYNAMODB_TABLE_NAME_COURSE = "ba_course";
    public static final String DYNAMODB_TABLE_NAME_COURSE_RESOURCE = "ba_course_resource";
    public static final String DYNAMODB_TABLE_NAME_CART = "ba_cart";
    public static final String DYNAMODB_TABLE_NAME_ORDER = "ba_order";
    public static final String DYNAMODB_TABLE_NAME_USER_COURSE = "ba_user_course";

    public static final String STRIPE_SECRET_KEY = "sk_test_UTifZqEZPT8SyQBTrWCSwfAn00xySX7uI7";
    public static final String ELASTIC_TRANSCODER_PIPELINE_ID = "1551221588927-5zpuyx";

    public static final String STATUS_SAVE = "SAVE";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";

    public static final String EVENT_MODIFY = "MODIFY";

}
