package com.brilliant.academe.domain.course;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetCourseLectureResponse implements Serializable {

    @JsonProperty("id")
    private String courseId;

    @JsonProperty("resources")
    private List<CourseSection> courseSection;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public List<CourseSection> getCourseSection() {
        return courseSection;
    }

    public void setCourseSection(List<CourseSection> courseSection) {
        this.courseSection = courseSection;
    }
}
