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
    public static final String CF_IMAGES_ORIGIN_PATH = "images";
    public static final String CF_VIDEOS_ORIGIN_PATH = "content";
    public static final String S3_PROFILE_FOLDER = "profile";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String STATUS_IN_PROCESS = "INPROCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String EVENT_MODIFY = "MODIFY";
    public static final String EVENT_INSERT = "INSERT";
    public static final String NOT_AVAILABLE = "NOT AVAILABLE";
    public static final String CONFIG_ID = "1";
    public static final String LOOKUP_ID = "1";
    public static final String STATUS_NO = "N";
    public static final String STATUS_YES = "Y";
    public static final String HLS_M3U8_FORMAT = "_hls.m3u8";

}
