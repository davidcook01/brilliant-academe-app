package com.brilliant.academe.domain.course;

import com.brilliant.academe.domain.enrollment.EnrollCourseInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetStudentCourseResponse implements Serializable {

    private List<EnrollCourseInfo> courses;

    public List<EnrollCourseInfo> getCourses() {
        return courses;
    }

    public void setCourses(List<EnrollCourseInfo> courses) {
        this.courses = courses;
    }
}
