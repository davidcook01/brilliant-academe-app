package com.brilliant.academe.domain.course;

import java.io.Serializable;
import java.util.List;

public class GetCourseResponse implements Serializable {

    private String courseId;
    private String instructorId;
    private String instructorName;
    private List courseCategories;
    private String courseCoverImage;
    private String courseDescription;
    private String courseName;
    private String courseRating;
    private List courseSection;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(String instructorId) {
        this.instructorId = instructorId;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public List getCourseCategories() {
        return courseCategories;
    }

    public void setCourseCategories(List courseCategories) {
        this.courseCategories = courseCategories;
    }

    public String getCourseCoverImage() {
        return courseCoverImage;
    }

    public void setCourseCoverImage(String courseCoverImage) {
        this.courseCoverImage = courseCoverImage;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(String courseRating) {
        this.courseRating = courseRating;
    }

    public List getCourseSection() {
        return courseSection;
    }

    public void setCourseSection(List courseSection) {
        this.courseSection = courseSection;
    }
}
