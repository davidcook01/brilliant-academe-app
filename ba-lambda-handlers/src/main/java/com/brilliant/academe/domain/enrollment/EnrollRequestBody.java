package com.brilliant.academe.domain.enrollment;

import java.io.Serializable;

public class EnrollRequestBody implements Serializable {

    private String courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
