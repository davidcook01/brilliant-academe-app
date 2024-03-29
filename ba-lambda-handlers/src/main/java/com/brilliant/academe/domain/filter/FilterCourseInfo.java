package com.brilliant.academe.domain.filter;

import com.brilliant.academe.constant.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterCourseInfo implements Serializable {

    @JsonProperty("id")
    private String courseId;

    private String courseName;

    @JsonProperty("description")
    private String courseDescription;

    private String coverImage;

    private String instructorId;

    private String instructorName;

    private Float courseDuration;

    private Float courseRating;

    private String courseType;

    @JsonProperty("price")
    private BigDecimal coursePrice;

    @JsonProperty("discountedPrice")
    private BigDecimal discountedCoursePrice;

    private Integer totalEnrolled;

    private Integer totalRating;

    private String reviewed = Constant.STATUS_NO;

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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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

    public Integer getTotalEnrolled() {
        return totalEnrolled;
    }

    public void setTotalEnrolled(Integer totalEnrolled) {
        this.totalEnrolled = totalEnrolled;
    }

    public Integer getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(Integer totalRating) {
        this.totalRating = totalRating;
    }

    public String getReviewed() {
        return reviewed;
    }

    public void setReviewed(String reviewed) {
        this.reviewed = reviewed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterCourseInfo that = (FilterCourseInfo) o;
        return courseId.equals(that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId);
    }
}
