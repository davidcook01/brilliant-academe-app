package com.brilliant.academe.domain.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetCourseLectureResponse implements Serializable {

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
