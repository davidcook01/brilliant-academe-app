package com.brilliant.academe.domain.course;

import com.brilliant.academe.domain.user.Instructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetCourseResponse implements Serializable {

    @JsonProperty("id")
    private String courseId;

    private String courseName;

    @JsonProperty("description")
    private String courseDescription;

    private String detailedDescription;

    private String coverImage;

    private Float courseDuration;

    private Float courseRating;

    private String courseType;

    @JsonProperty("price")
    private BigDecimal coursePrice;

    @JsonProperty("discountedPrice")
    private BigDecimal discountedCoursePrice;

    private String skuId;

    private Integer totalEnrolled;

    private Integer totalRating;

    private Instructor instructorDetails;

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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
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

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
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

    public Instructor getInstructorDetails() {
        return instructorDetails;
    }

    public void setInstructorDetails(Instructor instructorDetails) {
        this.instructorDetails = instructorDetails;
    }
}
