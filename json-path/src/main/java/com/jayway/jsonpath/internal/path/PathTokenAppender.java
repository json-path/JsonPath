package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.internal.path.token.PathToken;

public interface PathTokenAppender {
    PathTokenAppender appendPathToken(PathToken next);
}
