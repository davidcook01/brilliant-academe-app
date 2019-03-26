package com.brilliant.academe.domain.course;

import java.io.Serializable;

public class GetCourseLectureRequest implements Serializable {

    private String courseId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
