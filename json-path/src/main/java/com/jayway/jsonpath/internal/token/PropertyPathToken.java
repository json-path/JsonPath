/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.jsonpath.internal.token;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.Utils;

import java.util.List;

/**
 *
 */
class PropertyPathToken extends PathToken {

    private final List<String> properties;

    public PropertyPathToken(List<String> properties) {
        this.properties = properties;
    }

    public List<String> getProperties() {
        return properties;
    }

    @Override
    public void evaluate(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        if (!ctx.jsonProvider().isMap(model)) {

            String m = model == null ? "null" : model.getClass().getName();

            throw new PathNotFoundException("Expected to find an object with property " + getPathFragment() + " but found '" + m + "'. This is not a json object according to the JsonProvider: '" + ctx.configuration().jsonProvider().getClass().getName() + "'.");
        }

        handleObjectProperty(currentPath, model, ctx, properties);
    }

    @Override
    public boolean isTokenDefinite() {
        return true;
    }

    @Override
    public String getPathFragment() {
        return new StringBuilder()
                .append("[")
                .append(Utils.join(",", "'", properties))
                .append("]").toString();
    }
}
