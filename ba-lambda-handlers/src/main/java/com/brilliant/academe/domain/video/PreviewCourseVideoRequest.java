package com.brilliant.academe.domain.video;

import java.io.Serializable;

public class PreviewCourseVideoRequest implements Serializable {

    private String courseId;
    private String lectureId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }
}
