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

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.jayway.jsonpath.internal.Utils.notNull;

/**
 *
 */
public class EvaluationContextImpl implements EvaluationContext {

    private final Configuration configuration;
    private final Object valueResult;
    private final Object pathResult;
    private final Path path;
    private final Object rootDocument;
    private final HashMap<Path, Object> documentEvalCache = new HashMap<Path, Object>();
    private int resultIndex = 0;


    public EvaluationContextImpl(Path path, Object rootDocument, Configuration configuration) {
        notNull(path, "path can not be null");
        notNull(rootDocument, "root can not be null");
        notNull(configuration, "configuration can not be null");
        this.path = path;
        this.rootDocument = rootDocument;
        this.configuration = configuration;
        this.valueResult = configuration.jsonProvider().createArray();
        this.pathResult = configuration.jsonProvider().createArray();
    }

    public HashMap<Path, Object> documentEvalCache() {
        return documentEvalCache;
    }

    public void addResult(String path, Object model) {
        configuration.jsonProvider().setProperty(valueResult, resultIndex, model);
        configuration.jsonProvider().setProperty(pathResult, resultIndex, path);
        resultIndex++;
    }

    public JsonProvider jsonProvider() {
        return configuration.jsonProvider();
    }

    public Set<Option> options() {
        return configuration.getOptions();
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public Object rootDocument() {
        return rootDocument;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        if (path.isDefinite()) {
            if(resultIndex == 0){
                throw new PathNotFoundException("No results for path: " + path.toString());
            }
            return (T) jsonProvider().getArrayIndex(valueResult, 0);
        }
        return (T)valueResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getPath() {
        if(resultIndex == 0){
            throw new PathNotFoundException("No results for path: " + path.toString());
        }
        return (T)pathResult;
    }

    @Override
    public List<String> getPathList() {
        List<String> res = new ArrayList<String>();
        if(resultIndex > 0){
            Iterable<?> objects = configuration.jsonProvider().toIterable(pathResult);
            for (Object o : objects) {
                res.add((String)o);
            }
        }
        return res;
    }

}
