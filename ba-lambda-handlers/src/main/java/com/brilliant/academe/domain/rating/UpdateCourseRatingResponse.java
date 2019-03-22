package com.brilliant.academe.domain.rating;

import java.io.Serializable;

public class UpdateCourseRatingResponse implements Serializable {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
