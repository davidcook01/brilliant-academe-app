package com.brilliant.academe.domain.filter;

import java.io.Serializable;
import java.util.List;

public class FilterCourseResponse implements Serializable {

    private List<FilterCourseInfo> courses;

    public List<FilterCourseInfo> getCourses() {
        return courses;
    }

    public void setCourses(List<FilterCourseInfo> courses) {
        this.courses = courses;
    }
}
