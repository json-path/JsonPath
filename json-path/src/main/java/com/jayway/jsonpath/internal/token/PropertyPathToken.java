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

import java.util.List;
import java.util.logging.Logger;

import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.Utils;

/**
 *
 */
public class PropertyPathToken extends PathToken {

    protected static Logger log = Logger.getLogger("com.jayway.jsonpath");

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
            throw new PathNotFoundException("Property " + getPathFragment() + " not found in path " + currentPath);
        }

        handleObjectProperty(currentPath, model, ctx, properties);
    }

    @Override
    public boolean checkForMatch(TokenStack stack, int idx)
    {
        assert(stack.getStack().size() > idx);
        TokenStackElement curr = stack.getStack().get(idx);

        if (curr.getType() == TokenType.OBJECT_TOKEN) {
            ObjectToken token = (ObjectToken)curr;
            if (token.getKey() != null) {
                for (String checkKey : properties) {
                    log.info("checking key " + checkKey + " against " + token.getKey());
                    if (token.getKey().equals(checkKey)) {
                        if (isLeaf()) {
                            return stack.getStack().size() - 1 == idx;
                        }
                        return next().checkForMatch(stack, idx + 1);
                    }
                }
            }
        }

        return false;
    }

    @Override
    boolean isTokenDefinite() {
        return true;
    }

    @Override
    public String getPathFragment() {
        return new StringBuilder()
                .append("[")
                .append(Utils.join(", ", "'", properties))
                .append("]").toString();
    }
}
