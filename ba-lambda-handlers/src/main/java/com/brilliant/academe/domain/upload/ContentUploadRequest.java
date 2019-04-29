package com.brilliant.academe.domain.upload;

import java.io.Serializable;

public class ContentUploadRequest implements Serializable {

    private String token;
    private ContentUploadRequestBody body;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ContentUploadRequestBody getBody() {
        return body;
    }

    public void setBody(ContentUploadRequestBody body) {
        this.body = body;
    }
}
