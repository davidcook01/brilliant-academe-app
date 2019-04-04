package com.brilliant.academe.domain.video;

import java.io.Serializable;

public class CourseVideoResponse implements Serializable {

    private String signedUrl;
    private String message;

    public String getSignedUrl() {
        return signedUrl;
    }

    public void setSignedUrl(String signedUrl) {
        this.signedUrl = signedUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
