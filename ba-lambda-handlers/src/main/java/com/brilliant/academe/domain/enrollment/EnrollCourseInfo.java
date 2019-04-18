package com.brilliant.academe.domain.enrollment;

import com.brilliant.academe.util.CommonUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrollCourseInfo implements Serializable {

    @JsonProperty("id")
    private String courseId;

    private String courseName;

    @JsonProperty("description")
    private String courseDescription;

    private String coverImage;

    private String instructorId;

    private String instructorName;

    private Integer percentageCompleted;

    private Float courseDuration;

    private Float courseRating;

    public Float getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(Float courseRating) {
        this.courseRating = courseRating;
    }

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
        this.coverImage = CommonUtils.getSignedUrlForObject(coverImage);
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

    public Integer getPercentageCompleted() {
        return percentageCompleted;
    }

    public void setPercentageCompleted(Integer percentageCompleted) {
        this.percentageCompleted = percentageCompleted;
    }

    public Float getCourseDuration() {
        return courseDuration;
    }

    public void setCourseDuration(Float courseDuration) {
        this.courseDuration = courseDuration;
    }
}
