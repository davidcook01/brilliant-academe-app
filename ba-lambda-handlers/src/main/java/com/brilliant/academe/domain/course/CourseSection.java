package com.brilliant.academe.domain.course;

import java.io.Serializable;
import java.util.List;

public class CourseSection implements Serializable {

    private String sectionName;
    private String sectionDescription;
    private List<CourseLecture> lectures;

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionDescription() {
        return sectionDescription;
    }

    public void setSectionDescription(String sectionDescription) {
        this.sectionDescription = sectionDescription;
    }

    public List<CourseLecture> getLectures() {
        return lectures;
    }

    public void setLectures(List<CourseLecture> lectures) {
        this.lectures = lectures;
    }
}
