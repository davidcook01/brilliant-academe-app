package com.brilliant.academe.domain.enrollment;

import java.io.Serializable;

public class EnrollCourseRequest implements Serializable {

    private String userId;
    private EnrollCourseRequestInfo body;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public EnrollCourseRequestInfo getBody() {
        return body;
    }

    public void setBody(EnrollCourseRequestInfo body) {
        this.body = body;
    }
}
