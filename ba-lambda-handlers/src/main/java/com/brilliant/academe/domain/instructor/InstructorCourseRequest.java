package com.brilliant.academe.domain.instructor;

import java.io.Serializable;

public class InstructorCourseRequest implements Serializable {

    private String type;
    private String operation;
    private InstructorCourse course;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public InstructorCourse getCourse() {
        return course;
    }

    public void setCourse(InstructorCourse course) {
        this.course = course;
    }
}
