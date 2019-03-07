package com.brilliant.academe.domain.enrollment;

import java.io.Serializable;

public class EnrollCourseResponse implements Serializable {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
