package com.brilliant.academe.domain.lookup;

import com.brilliant.academe.domain.course.CourseCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LookupMasterResponse implements Serializable {

    private List<String> courseLevel;
    private List<String> courseType;
    private List<CourseCategory> courseCategories;

    public List<String> getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(List<String> courseLevel) {
        this.courseLevel = courseLevel;
    }

    public List<String> getCourseType() {
        return courseType;
    }

    public void setCourseType(List<String> courseType) {
        this.courseType = courseType;
    }

    public List<CourseCategory> getCourseCategories() {
        return courseCategories;
    }

    public void setCourseCategories(List<CourseCategory> courseCategories) {
        this.courseCategories = courseCategories;
    }
}
