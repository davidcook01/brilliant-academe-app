package com.brilliant.academe.domain.rating;

import java.io.Serializable;

public class CourseRatingInfo implements Serializable {

    private Float courseRating;

    public Float getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(Float courseRating) {
        this.courseRating = courseRating;
    }
}
