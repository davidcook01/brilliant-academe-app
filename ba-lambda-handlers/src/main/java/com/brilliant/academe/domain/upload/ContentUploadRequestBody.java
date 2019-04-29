package com.brilliant.academe.domain.upload;

import java.io.Serializable;

public class ContentUploadRequestBody implements Serializable {

    private String name;    //course name
    private String path;    //full path of the file
    private String key;  //name of file with extension
    private String type; //image or content

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
