package com.brilliant.academe.domain.instructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructorCourseSection implements Serializable {

    private String sectionId;
    private String sectionName;
    private String sectionDescription;
    private Integer sectionOrder;
    private List<InstructorCourseLecture> lectures;

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

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

    public Integer getSectionOrder() {
        return sectionOrder;
    }

    public void setSectionOrder(Integer sectionOrder) {
        this.sectionOrder = sectionOrder;
    }

    public List<InstructorCourseLecture> getLectures() {
        return lectures;
    }

    public void setLectures(List<InstructorCourseLecture> lectures) {
        this.lectures = lectures;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstructorCourseSection that = (InstructorCourseSection) o;
        return sectionId.equals(that.sectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionId);
    }
}
