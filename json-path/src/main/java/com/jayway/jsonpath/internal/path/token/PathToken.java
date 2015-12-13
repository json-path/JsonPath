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
package com.jayway.jsonpath.internal.path.token;

import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.PathFunction;
import com.jayway.jsonpath.internal.path.EvaluationContextImpl;

public abstract class PathToken {

    private PathToken prev;
    private PathToken next;
    private Boolean definite = null;
    private Boolean upstreamDefinite = null;

    public PathToken appendTailToken(PathToken next) {
        this.next = next;
        this.next.prev = this;
        return next;
    }

    public PathToken prev(){
        return prev;
    }

    public PathToken next() {
        if (isLeaf()) {
            throw new IllegalStateException("Current path token is a leaf");
        }
        return next;
    }

    public boolean isLeaf() {
        return next == null;
    }

    public boolean isRoot() {
        return  prev == null;
    }

    public boolean isUpstreamDefinite() {
        if (upstreamDefinite == null) {
            upstreamDefinite = isRoot() || prev.isTokenDefinite() && prev.isUpstreamDefinite();
        }
        return upstreamDefinite;
    }

    public int getTokenCount() {
        int cnt = 1;
        PathToken token = this;

        while (!token.isLeaf()){
            token = token.next();
            cnt++;
        }
        return cnt;
    }

    public boolean isPathDefinite() {
        if(definite != null){
            return definite.booleanValue();
        }
        boolean isDefinite = isTokenDefinite();
        if (isDefinite && !isLeaf()) {
            isDefinite = next.isPathDefinite();
        }
        definite = isDefinite;
        return isDefinite;
    }

    @Override
    public String toString() {
        if (isLeaf()) {
            return getPathFragment();
        } else {
            return getPathFragment() + next().toString();
        }
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void invoke(PathFunction pathFunction, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        ctx.addResult(currentPath, parent, pathFunction.invoke(currentPath, parent, model, ctx));
    }

    public abstract boolean isTokenDefinite();

    protected abstract String getPathFragment();

}
