package com.brilliant.academe.domain.rating;

import java.io.Serializable;

public class UpdateCourseRatingRequest implements Serializable {

    private String userId;
    private String courseId;
    private CourseRatingInfo body;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public CourseRatingInfo getBody() {
        return body;
    }

    public void setBody(CourseRatingInfo body) {
        this.body = body;
    }
}
