package com.jayway.jsonpath.web.bench;

import com.jayway.jsonpath.internal.JsonFormatter;

import java.util.List;

public class Result {

    public final String provider;
    public final String active;
    public final long time;
    public final String result;
    public final String error;

    public Result(String provider, long time, String result, String error) {
        this.provider = provider;
        this.active = provider == "jayway" ? "active" : "";
        this.time = time;
        this.result = result != null ? JsonFormatter.prettyPrint(result) : result;
        this.error = error;
    }
}