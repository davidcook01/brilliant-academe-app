package com.brilliant.academe.domain.course;

import java.io.Serializable;

public class CreateCourseReviewRequest implements Serializable {

    private String id;
    private String userId;
    private String reviewed;
    private CreateCourseRequest info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReviewed() {
        return reviewed;
    }

    public void setReviewed(String reviewed) {
        this.reviewed = reviewed;
    }

    public CreateCourseRequest getInfo() {
        return info;
    }

    public void setInfo(CreateCourseRequest info) {
        this.info = info;
    }
}
