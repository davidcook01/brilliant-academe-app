package com.brilliant.academe.domain.enrollment;

import java.io.Serializable;

public class EnrollCourseRequest implements Serializable {

    private EnrollRequestBody body;
    private String token;

    public EnrollRequestBody getBody() {
        return body;
    }

    public void setBody(EnrollRequestBody body) {
        this.body = body;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
