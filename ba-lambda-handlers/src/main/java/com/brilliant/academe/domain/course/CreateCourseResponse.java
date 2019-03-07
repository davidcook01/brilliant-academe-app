package com.brilliant.academe.domain.course;

import java.io.Serializable;

public class CreateCourseResponse implements Serializable {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
