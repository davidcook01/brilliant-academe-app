package com.brilliant.academe.domain.course;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseLecture implements Serializable {

    private String lectureId;
    private String lectureTitle;
    private String lectureLink;
    private BigDecimal lectureDuration;
    private boolean isPreviewAvailable;
    private String lectureFile;
    private Integer lectureOrder;
    private List<CourseMaterial> materials;

    public String getLectureId() {
        return lectureId;
    }

    public void setLectureId(String lectureId) {
        this.lectureId = lectureId;
    }

    public String getLectureTitle() {
        return lectureTitle;
    }

    public void setLectureTitle(String lectureTitle) {
        this.lectureTitle = lectureTitle;
    }

    public String getLectureLink() {
        return lectureLink;
    }

    public void setLectureLink(String lectureLink) {
        this.lectureLink = lectureLink;
    }

    public BigDecimal getLectureDuration() {
        return lectureDuration;
    }

    public void setLectureDuration(BigDecimal lectureDuration) {
        this.lectureDuration = lectureDuration;
    }

    public boolean isPreviewAvailable() {
        return isPreviewAvailable;
    }

    public void setPreviewAvailable(boolean previewAvailable) {
        isPreviewAvailable = previewAvailable;
    }

    public String getLectureFile() {
        return lectureFile;
    }

    public void setLectureFile(String lectureFile) {
        this.lectureFile = lectureFile;
    }

    public Integer getLectureOrder() {
        return lectureOrder;
    }

    public void setLectureOrder(Integer lectureOrder) {
        this.lectureOrder = lectureOrder;
    }

    public List<CourseMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<CourseMaterial> materials) {
        this.materials = materials;
    }
}
