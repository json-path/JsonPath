package com.jayway.jsonpath.internal.token;

public interface PathTokenAppender {
    PathTokenAppender appendPathToken(PathToken next);
}
