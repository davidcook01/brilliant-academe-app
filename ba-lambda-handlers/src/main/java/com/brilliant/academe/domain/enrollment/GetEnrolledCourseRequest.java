package com.brilliant.academe.domain.enrollment;

import java.io.Serializable;

public class GetEnrolledCourseRequest implements Serializable {

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
