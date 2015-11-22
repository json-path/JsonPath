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
package com.jayway.jsonpath.internal.path;

import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.PathRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

/**
 *
 */
public class ArrayPathToken extends PathToken {

    private static final Logger logger = LoggerFactory.getLogger(ArrayPathToken.class);

    private final ArraySliceOperation arraySliceOperation;
    private final ArrayIndexOperation arrayIndexOperation;

    ArrayPathToken(final ArraySliceOperation arraySliceOperation) {
        this.arraySliceOperation = arraySliceOperation;
        this.arrayIndexOperation = null;
    }

    ArrayPathToken(final ArrayIndexOperation arrayIndexOperation) {
        this.arrayIndexOperation = arrayIndexOperation;
        this.arraySliceOperation = null;
    }

    @Override
    public void evaluate(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        if (! checkArrayModel(currentPath, model, ctx))
            return;
        if(arraySliceOperation != null){
            evaluateSliceOperation(currentPath, parent, model, ctx);
        } else {
            evaluateIndexOperation(currentPath, parent, model, ctx);
        }

    }

    public void evaluateIndexOperation(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {

        if (! checkArrayModel(currentPath, model, ctx))
            return;

        if(arrayIndexOperation.isSingleIndexOperation()){
            handleArrayIndex(arrayIndexOperation.indexes().get(0), currentPath, model, ctx);
        } else {
            for (Integer index : arrayIndexOperation.indexes()) {
                handleArrayIndex(index, currentPath,  model, ctx);
            }
        }
    }

    public void evaluateSliceOperation(String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {

        if (! checkArrayModel(currentPath, model, ctx))
            return;

        switch (arraySliceOperation.operation()) {
            case SLICE_FROM:
                sliceFrom(arraySliceOperation, currentPath, parent, model, ctx);
                break;
            case SLICE_BETWEEN:
                sliceBetween(arraySliceOperation, currentPath, parent, model, ctx);
                break;
            case SLICE_TO:
                sliceTo(arraySliceOperation, currentPath, parent, model, ctx);
                break;
        }
    }

    public void sliceFrom(ArraySliceOperation operation, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        int length = ctx.jsonProvider().length(model);
        int from = operation.from();
        if (from < 0) {
            //calculate slice start from array length
            from = length + from;
        }
        from = Math.max(0, from);

        logger.debug("Slice from index on array with length: {}. From index: {} to: {}. Input: {}", length, from, length - 1, toString());

        if (length == 0 || from >= length) {
            return;
        }
        for (int i = from; i < length; i++) {
            handleArrayIndex(i, currentPath, model, ctx);
        }
    }

    public void sliceBetween(ArraySliceOperation operation, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        int length = ctx.jsonProvider().length(model);
        int from = operation.from();
        int to = operation.to();

        to = Math.min(length, to);

        if (from >= to || length == 0) {
            return;
        }

        logger.debug("Slice between indexes on array with length: {}. From index: {} to: {}. Input: {}", length, from, to, toString());

        for (int i = from; i < to; i++) {
            handleArrayIndex(i, currentPath, model, ctx);
        }
    }

    public void sliceTo(ArraySliceOperation operation, String currentPath, PathRef parent, Object model, EvaluationContextImpl ctx) {
        int length = ctx.jsonProvider().length(model);
        if (length == 0) {
            return;
        }
        int to = operation.to();
        if (to < 0) {
            //calculate slice end from array length
            to = length + to;
        }
        to = Math.min(length, to);

        logger.debug("Slice to index on array with length: {}. From index: 0 to: {}. Input: {}", length, to, toString());

        for (int i = 0; i < to; i++) {
            handleArrayIndex(i, currentPath, model, ctx);
        }
    }

    @Override
    public String getPathFragment() {
        if(arrayIndexOperation != null){
            return arrayIndexOperation.toString();
        } else {
            return arraySliceOperation.toString();
        }
    }

    @Override
    public boolean isTokenDefinite() {
        if(arrayIndexOperation != null){
            return arrayIndexOperation.isSingleIndexOperation();
        } else {
            return false;
        }
    }

    /**
     * Check if model is non-null and array.
     * @param currentPath
     * @param model
     * @param ctx
     * @return false if current evaluation call must be skipped, true otherwise
     * @throws PathNotFoundException if model is null and evaluation must be interrupted
     * @throws InvalidPathException if model is not an array and evaluation must be interrupted
     */
    protected boolean checkArrayModel(String currentPath, Object model, EvaluationContextImpl ctx) {
        if (model == null){
            if (! isUpstreamDefinite()) {
                return false;
            } else {
                throw new PathNotFoundException("The path " + currentPath + " is null");
            }
        }
        if (!ctx.jsonProvider().isArray(model)) {
            if (! isUpstreamDefinite()) {
                return false;
            } else {
                throw new PathNotFoundException(format("Filter: %s can only be applied to arrays. Current context is: %s", toString(), model));
            }
        }
        return true;
    }
}
