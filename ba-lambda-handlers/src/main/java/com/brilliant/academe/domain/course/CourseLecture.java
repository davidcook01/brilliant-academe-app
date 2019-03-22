package com.brilliant.academe.domain.course;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CourseLecture implements Serializable {

    private String lectureTitle;
    private String lectureLink;
    private BigDecimal lectureDuration;
    private List<CourseMaterial> materials;

    public String getLectureTitle() {
        return lectureTitle;
    }

    public void setLectureTitle(String lectureTitle) {
        this.lectureTitle = lectureTitle;
    }

    public String getLectureLink() {
        return lectureLink;
    }

    public BigDecimal getLectureDuration() {
        return lectureDuration;
    }

    public void setLectureDuration(BigDecimal lectureDuration) {
        this.lectureDuration = lectureDuration;
    }

    public void setLectureLink(String lectureLink) {
        this.lectureLink = lectureLink;
    }

    public List<CourseMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<CourseMaterial> materials) {
        this.materials = materials;
    }
}
