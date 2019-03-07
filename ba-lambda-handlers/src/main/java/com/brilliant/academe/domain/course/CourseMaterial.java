package com.brilliant.academe.domain.course;

import java.io.Serializable;

public class CourseMaterial implements Serializable {

    private String materialName;
    private String materialLink;

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getMaterialLink() {
        return materialLink;
    }

    public void setMaterialLink(String materialLink) {
        this.materialLink = materialLink;
    }
}
