package com.brilliant.academe.domain.instructor;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InstructorCourseMaterial implements Serializable {

    private String materialId;
    private String materialName;
    private Integer materialOrder;
    private String materialLink;
    private String materialFile;

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public Integer getMaterialOrder() {
        return materialOrder;
    }

    public void setMaterialOrder(Integer materialOrder) {
        this.materialOrder = materialOrder;
    }

    public String getMaterialLink() {
        return materialLink;
    }

    public void setMaterialLink(String materialLink) {
        this.materialLink = materialLink;
    }

    public String getMaterialFile() {
        return materialFile;
    }

    public void setMaterialFile(String materialFile) {
        this.materialFile = materialFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InstructorCourseMaterial that = (InstructorCourseMaterial) o;
        return materialId.equals(that.materialId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materialId);
    }
}
