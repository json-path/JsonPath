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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.internal.EvaluationAbortException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.spi.json.JsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompiledPath implements Path {

    private static final Logger logger = LoggerFactory.getLogger(CompiledPath.class);

    private final RootPathToken root;

    private final boolean isRootPath;


    public CompiledPath(RootPathToken root, boolean isRootPath) {
        this.root = root;
        this.isRootPath = isRootPath;
    }

    @Override
    public boolean isRootPath() {
        return isRootPath;
    }

    @Override
    public EvaluationContext evaluate(Object document, Object rootDocument, Configuration configuration,
                                      boolean forUpdate) {
        if (logger.isDebugEnabled()) {
            logger.debug("Evaluating path: {}", toString());
        }

        EvaluationContextImpl ctx =
                new EvaluationContextImpl(this, rootDocument, configuration, forUpdate, rootDocument);
        PathRef op = ctx.forUpdate() ? PathRef.createRoot(rootDocument) : PathRef.NO_OP;
        if (root.isFunctionPath()) {
            // Remove the functionPath from the path.
            PathToken funcToken = root.chop();
            try {
                // Evaluate the path without the tail function.
                root.evaluate("", op, document, ctx);
                // Get the value of the evaluation to use as model when evaluating the function.
                Object arrayModel = ctx.getValue(false);

                EvaluationContextImpl retCtx;
                if (!root.isPathDefinite() && isArrayOfArrays(ctx, arrayModel)) {
                    // Special case: non-definite paths that evaluate to an array of arrays will have the function
                    // applied to each array. An array of the results of the function call(s) will be returned.
                    Object array = ctx.configuration().jsonProvider().createArray();
                    for (int i = 0; i < ctx.configuration().jsonProvider().length(arrayModel); i++) {
                        Object model = ctx.configuration().jsonProvider().getArrayIndex(arrayModel, i);
                        EvaluationContextImpl valCtx =
                                evaluateFunction(funcToken, model, configuration, rootDocument, op);
                        Object val = valCtx.getValue(false);
                        ctx.configuration().jsonProvider().setArrayIndex(array, i, val);
                    }

                    retCtx = createFunctionEvaluationContext(funcToken, rootDocument, configuration, rootDocument);
                    retCtx.addResult(root.getPathFragment(), op, array);
                } else {
                    // Normal case: definite paths and non-definite paths that don't evaluate to an array of arrays
                    // (such as those that evaluate to an array of numbers) will have the function applied to the
                    // result of the original evaluation (which should be a 1-dimensional array). A single result
                    // value will be returned.
                    retCtx = evaluateFunction(funcToken, arrayModel, configuration, rootDocument, op);
                }

                return retCtx;
            } catch (EvaluationAbortException abort) {
            } finally {
                // Put the functionPath back on the original path so that caching works.
                root.append(funcToken);
            }
        } else {
            try {
                root.evaluate("", op, document, ctx);
                return ctx;
            } catch (EvaluationAbortException abort) {
            }
        }

        return ctx;
    }

    private boolean isArrayOfArrays(EvaluationContext ctx, Object model) {
        // Is the model an Array containing Arrays.
        JsonProvider jsonProvider = ctx.configuration().jsonProvider();
        if (!jsonProvider.isArray(model)) {
            return false;
        }
        if (jsonProvider.length(model) <= 0) {
            return false;
        }
        Object item = jsonProvider.getArrayIndex(model, 0);
        return jsonProvider.isArray(item);
    }

    private EvaluationContextImpl evaluateFunction(PathToken funcToken, Object model, Configuration configuration, Object rootDocument,
                                    PathRef op) {
        // Evaluate the function on the given model.
        EvaluationContextImpl newCtx = createFunctionEvaluationContext(funcToken, model, configuration, rootDocument);
        funcToken.evaluate("", op, model, newCtx);
        return newCtx;
    }

    private EvaluationContextImpl createFunctionEvaluationContext(PathToken funcToken, Object model,
                                                                  Configuration configuration, Object rootDocument) {
        RootPathToken newRoot = PathTokenFactory.createRootPathToken(root.getRootToken());
        newRoot.append(funcToken);
        CompiledPath newCPath = new CompiledPath(newRoot, true);
        return new EvaluationContextImpl(newCPath, model, configuration, false, rootDocument);
    }

    @Override
    public EvaluationContext evaluate(Object document, Object rootDocument, Configuration configuration){
        return evaluate(document, rootDocument, configuration, false);
    }

    @Override
    public boolean isDefinite() {
        return root.isPathDefinite();
    }

    @Override
    public boolean isFunctionPath() {
        return root.isFunctionPath();
    }

    @Override
    public String toString() {
        return root.toString();
    }
}
