package com.brilliant.academe.domain.user;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Map;

@JsonSerialize
public class CognitoPostConfirmationRequest {

    private int version;
    private String region;
    private String userPoolId;
    private String userName;
    private Map<String, String> callerContext;
    private String triggerSource;
    private Request request;
    private Response response;

    public CognitoPostConfirmationRequest() {
    }

    public CognitoPostConfirmationRequest(int version, String region, String userPoolId, String userName, Map<String, String> callerContext, String triggerSource, Request request, Response response) {
        this.version = version;
        this.region = region;
        this.userPoolId = userPoolId;
        this.userName = userName;
        this.callerContext = callerContext;
        this.triggerSource = triggerSource;
        this.request = request;
        this.response = response;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUserPoolId() {
        return userPoolId;
    }

    public void setUserPoolId(String userPoolId) {
        this.userPoolId = userPoolId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Map<String, String> getCallerContext() {
        return callerContext;
    }

    public void setCallerContext(Map<String, String> callerContext) {
        this.callerContext = callerContext;
    }

    public String getTriggerSource() {
        return triggerSource;
    }

    public void setTriggerSource(String triggerSource) {
        this.triggerSource = triggerSource;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public Response getResponse() {
        return response;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    @JsonSerialize
    public static class Request {
        private Map<String, String> userAttributes;
        public Request(Map<String, String> userAttr) {
            userAttributes = userAttr;
        }

        public Map<String, String> getUserAttributes() {
            return userAttributes;
        }

        public void setUserAttributes(Map<String, String> userAttributes) {
            this.userAttributes = userAttributes;
        }

        public Request() {
        }
    }

    @JsonSerialize
    public static class Response {
        public Response() {
        }
    }

}
