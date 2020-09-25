package com.jayway.jsonpath;

/**
 * Not Found Result property holder
 */
public class NotFoundResult {
    private String path;
    private String property;

    public NotFoundResult(String path, String property) {
        this.path = path;
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    public String getPath() {
        return path;
    }
}