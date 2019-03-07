package com.brilliant.academe.domain.course;

import java.io.Serializable;

public class GetStudentCourseRequest implements Serializable {

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
