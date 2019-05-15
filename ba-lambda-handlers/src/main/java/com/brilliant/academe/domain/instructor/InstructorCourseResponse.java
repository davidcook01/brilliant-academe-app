package com.brilliant.academe.domain.instructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructorCourseResponse implements Serializable {

    private String message;
    private String id;
    private InstructorCourse course;
    private List<InstructorCourseSection> sections;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public InstructorCourse getCourse() {
        return course;
    }

    public void setCourse(InstructorCourse course) {
        this.course = course;
    }

    public List<InstructorCourseSection> getSections() {
        return sections;
    }

    public void setSections(List<InstructorCourseSection> sections) {
        this.sections = sections;
    }
}
