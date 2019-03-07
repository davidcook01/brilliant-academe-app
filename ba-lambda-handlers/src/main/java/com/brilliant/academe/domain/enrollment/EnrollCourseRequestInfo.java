package com.brilliant.academe.domain.enrollment;

import java.io.Serializable;
import java.util.List;

public class EnrollCourseRequestInfo implements Serializable {

    private List<EnrollCourseInfo> courses;

    public List<EnrollCourseInfo> getCourses() {
        return courses;
    }

    public void setCourses(List<EnrollCourseInfo> courses) {
        this.courses = courses;
    }
}
