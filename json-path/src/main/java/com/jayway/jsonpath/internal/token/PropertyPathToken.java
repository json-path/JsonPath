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

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.jayway.jsonpath.internal.Utils.onlyOneIsTrueNonThrow;

/**
 *
 */
public class PropertyPathToken extends PathToken {

    private final List<String> properties;

    public PropertyPathToken(List<String> properties) {
        if (properties.isEmpty()) {
            throw new InvalidPathException("Empty properties");
        }
        this.properties = properties;
    }

    public List<String> getProperties() {
        return properties;
    }

    public boolean singlePropertyCase() {
        return properties.size() == 1;
    }

    public boolean multiPropertyMergeCase() {
        return isLeaf() && properties.size() > 1;
    }

    public boolean multiPropertyIterationCase() {
        // Semantics of this case is the same as semantics of ArrayPathToken with INDEX_SEQUENCE operation.
        return ! isLeaf() && properties.size() > 1;
    }

    @Override
    public void evaluate(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        // Can't assert it in ctor because isLeaf() could be changed later on.
        assert onlyOneIsTrueNonThrow(singlePropertyCase(), multiPropertyMergeCase(), multiPropertyIterationCase());

        if (!ctx.jsonProvider().isMap(model)) {
            if (! isUpstreamDefinite()) {
                return;
            } else {
                throw new PathNotFoundException("Property " + getPathFragment() + " not found in path " + currentPath);
            }
        }

        if (singlePropertyCase() || multiPropertyMergeCase()) {
            handleObjectProperty(currentPath, model, ctx, properties);
            return;
        }

        assert multiPropertyIterationCase();
        final List<String> currentlyHandledProperty = new ArrayList<String>(1);
        currentlyHandledProperty.add(null);
        for (final String property : properties) {
            currentlyHandledProperty.set(0, property);
            handleObjectProperty(currentPath, model, ctx, currentlyHandledProperty);
        }
    }

    @Override
    public boolean isTokenDefinite() {
        // in case of leaf multiprops will be merged, so it's kinda definite
        return singlePropertyCase() || multiPropertyMergeCase();
    }

    @Override
    public String getPathFragment() {
        return new StringBuilder()
                .append("[")
                .append(Utils.join(",", "'", properties))
                .append("]").toString();
    }
}
