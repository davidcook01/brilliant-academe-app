package com.brilliant.academe.domain.course;

import java.io.Serializable;
import java.util.List;

public class CreateCourseRequest implements Serializable {

    private String courseName;
    private String courseDescription;
    private String coverImage;
    private List courseCategories;
    private String instructorId;
    private String instructorName;
    private String percentageCompletion;
    private String courseRating;
    private String myRating;
    private List<CourseSection> sections;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List getCourseCategories() {
        return courseCategories;
    }

    public void setCourseCategories(List courseCategories) {
        this.courseCategories = courseCategories;
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

    public String getPercentageCompletion() {
        return percentageCompletion;
    }

    public void setPercentageCompletion(String percentageCompletion) {
        this.percentageCompletion = percentageCompletion;
    }

    public String getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(String courseRating) {
        this.courseRating = courseRating;
    }

    public String getMyRating() {
        return myRating;
    }

    public void setMyRating(String myRating) {
        this.myRating = myRating;
    }

    public List<CourseSection> getSections() {
        return sections;
    }

    public void setSections(List<CourseSection> sections) {
        this.sections = sections;
    }
}