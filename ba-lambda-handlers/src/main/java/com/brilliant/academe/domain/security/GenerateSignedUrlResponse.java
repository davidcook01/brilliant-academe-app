package com.brilliant.academe.domain.security;

import java.io.Serializable;

public class GenerateSignedUrlResponse implements Serializable {

    private String signedUrl;

    public String getSignedUrl() {
        return signedUrl;
    }

    public void setSignedUrl(String signedUrl) {
        this.signedUrl = signedUrl;
    }
}
