package com.brilliant.academe.domain.instructor;

import com.brilliant.academe.domain.course.CourseCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructorCourseResponseInfo implements Serializable {

    @JsonProperty("id")
    private String courseId;

    private String courseName;

    @JsonProperty("description")
    private String courseDescription;

    private String detailedDescription;

    private List<CourseCategory> courseCategories;

    private String instructorId;

    private String instructorName;

    private String courseLevel;

    @JsonProperty("price")
    private BigDecimal coursePrice;

    @JsonProperty("discountedPrice")
    private BigDecimal discountedCoursePrice;

    private String courseType;

    private String coverImage;

    private String tags;

    @JsonProperty("resources")
    private List<InstructorCourseSection> sections;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

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

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<InstructorCourseSection> getSections() {
        return sections;
    }

    public void setSections(List<InstructorCourseSection> sections) {
        this.sections = sections;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstructorCourseResponseInfo that = (InstructorCourseResponseInfo) o;
        return Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
