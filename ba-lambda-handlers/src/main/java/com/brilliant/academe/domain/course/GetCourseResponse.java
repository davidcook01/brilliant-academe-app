package com.brilliant.academe.domain.course;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class GetCourseResponse implements Serializable {

    @JsonProperty("id")
    private String courseId;

    @JsonProperty("resources")
    private List courseSection;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List getCourseSection() {
        return courseSection;
    }

    public void setCourseSection(List courseSection) {
        this.courseSection = courseSection;
    }
}
