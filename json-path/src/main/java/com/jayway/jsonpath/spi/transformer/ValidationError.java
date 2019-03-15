package com.jayway.jsonpath.spi.transformer;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public abstract class ValidationError {

    private String errorCode;
    private String description;

    public ValidationError(String errorCode, ResourceBundle bundle) {
        this.errorCode = errorCode;
        this.description = bundle.containsKey(errorCode) ? bundle.getString(errorCode): errorCode;
    }

    public ValidationError(String errorCode,  ResourceBundle bundle, Object... params) {
        this.errorCode = errorCode;
        this.description = MessageFormat.format(bundle.getString(errorCode),  params);
    }

    public String getErrorCode() {
        return errorCode;
    }


    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return new StringBuilder().append("\t").append("errorCode=")
                .append(errorCode).append(" : ").append("description=")
                .append(description).append("\n").toString();
    }

}
