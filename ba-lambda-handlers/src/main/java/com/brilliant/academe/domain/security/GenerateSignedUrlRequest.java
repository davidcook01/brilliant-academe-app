package com.brilliant.academe.domain.security;

import java.io.Serializable;

public class GenerateSignedUrlRequest implements Serializable {

    private String objectName;

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }
}
