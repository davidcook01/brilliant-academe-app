package com.brilliant.academe.domain.instructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructorCourseLecture implements Serializable {

    private String lectureId;
    private String lectureTitle;
    private String lectureFile;
    private String lectureLink;
    private Integer lectureOrder;
    private BigDecimal lectureDuration;
    private boolean isPreviewAvailable;
    private List<InstructorCourseMaterial> materials;

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

    public String getLectureFile() {
        return lectureFile;
    }

    public void setLectureFile(String lectureFile) {
        this.lectureFile = lectureFile;
    }

    public String getLectureLink() {
        return lectureLink;
    }

    public void setLectureLink(String lectureLink) {
        this.lectureLink = lectureLink;
    }

    public Integer getLectureOrder() {
        return lectureOrder;
    }

    public void setLectureOrder(Integer lectureOrder) {
        this.lectureOrder = lectureOrder;
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

    public List<InstructorCourseMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<InstructorCourseMaterial> materials) {
        this.materials = materials;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstructorCourseLecture that = (InstructorCourseLecture) o;
        return lectureId.equals(that.lectureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lectureId);
    }
}
