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
    public EvaluationContext evaluate(Object document, Object rootDocument, Configuration configuration, boolean forUpdate) {
        if (logger.isDebugEnabled()) {
            logger.debug("Evaluating path: {}", toString());
        }

        EvaluationContextImpl ctx = new EvaluationContextImpl(this, rootDocument, configuration, forUpdate);
        try {
            PathRef op = ctx.forUpdate() ?  PathRef.createRoot(rootDocument) : PathRef.NO_OP;

            if (root.isFunctionPath()) {
                // Remove the functionPath and evaluate the resulting path.
                PathToken funcToken = root.chop();
                root.evaluate("", op, document, ctx);
                // Get the value of the evaluation to use as model when evaluating the function.
                Object arrayModel = ctx.getValue(false);

                // Evaluate the function on the model from the first evaluation.
                RootPathToken newRoot = new RootPathToken('x');
                newRoot.append(funcToken);
                CompiledPath newCPath = new CompiledPath(newRoot, true);
                EvaluationContextImpl newCtx = new EvaluationContextImpl(newCPath, arrayModel, configuration, false);
                funcToken.evaluate("", op, arrayModel, newCtx);
                return newCtx;
            } else {
                root.evaluate("", op, document, ctx);
                return ctx;
            }
        } catch (EvaluationAbortException abort){};

        return ctx;
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
