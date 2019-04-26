package com.brilliant.academe.domain.course;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CreateCourseRequest implements Serializable {

    private String courseName;
    private String courseDescription;
    private String detailedDescription;
    private String coverImage;
    private List<CourseCategory> courseCategories;
    private String instructorId;
    private String instructorName;
    private String courseLevel;
    private BigDecimal coursePrice;
    private BigDecimal discountedCoursePrice;
    private Float courseDuration;
    private Float courseRating;
    private String courseType;
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

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<CourseCategory> getCourseCategories() {
        return courseCategories;
    }

    public void setCourseCategories(List<CourseCategory> courseCategories) {
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

    public String getCourseLevel() {
        return courseLevel;
    }

    public void setCourseLevel(String courseLevel) {
        this.courseLevel = courseLevel;
    }

    public BigDecimal getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(BigDecimal coursePrice) {
        this.coursePrice = coursePrice;
    }

    public BigDecimal getDiscountedCoursePrice() {
        return discountedCoursePrice;
    }

    public void setDiscountedCoursePrice(BigDecimal discountedCoursePrice) {
        this.discountedCoursePrice = discountedCoursePrice;
    }

    public Float getCourseDuration() {
        return courseDuration;
    }

    public void setCourseDuration(Float courseDuration) {
        this.courseDuration = courseDuration;
    }

    public Float getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(Float courseRating) {
        this.courseRating = courseRating;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public List<CourseSection> getSections() {
        return sections;
    }

    public void setSections(List<CourseSection> sections) {
        this.sections = sections;
    }
}