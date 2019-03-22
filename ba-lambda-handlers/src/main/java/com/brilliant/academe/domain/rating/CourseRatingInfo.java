package com.brilliant.academe.domain.rating;

import java.io.Serializable;
import java.math.BigDecimal;

public class CourseRatingInfo implements Serializable {

    private BigDecimal courseRating;

    public BigDecimal getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(BigDecimal courseRating) {
        this.courseRating = courseRating;
    }
}
