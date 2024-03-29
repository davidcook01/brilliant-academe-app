package com.brilliant.academe.domain.rating;

import java.io.Serializable;

public class UpdateCourseRatingRequest implements Serializable {

    private String token;
    private String courseId;
    private CourseRatingInfo body;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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
