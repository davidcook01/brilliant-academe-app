package com.brilliant.academe.domain.course;

import com.brilliant.academe.domain.enrollment.EnrollCourseInfoList;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetStudentCourseResponse implements Serializable {

    @JsonProperty("enrolled_courses")
    private EnrollCourseInfoList enrolledCourses;

    public EnrollCourseInfoList getEnrolledCourses() {
        return enrolledCourses;
    }

    public void setEnrolledCourses(EnrollCourseInfoList enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }
}
