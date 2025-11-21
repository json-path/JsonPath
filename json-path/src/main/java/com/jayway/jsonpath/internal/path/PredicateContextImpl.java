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
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.spi.mapper.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.regex.Pattern;

public class PredicateContextImpl implements Predicate.PredicateContext {

    private static final Logger logger = LoggerFactory.getLogger(PredicateContextImpl.class);

    private final Object contextDocument;
    private final Object rootDocument;
    private final Configuration configuration;
    private final HashMap<Path, Object> documentPathCache;

    private final HashMap<String, Pattern> compiledPatternCache;

    public PredicateContextImpl(Object contextDocument, Object rootDocument, Configuration configuration, HashMap<Path, Object> documentPathCache) {
        this.contextDocument = contextDocument;
        this.rootDocument = rootDocument;
        this.configuration = configuration;
        this.documentPathCache = documentPathCache;
        this.compiledPatternCache = new HashMap<String, Pattern>();
    }

    public Object evaluate(Path path){
        Object result;
        if(path.isRootPath()){
            if(documentPathCache.containsKey(path)){
                logger.debug("Using cached result for root path: " + path.toString());
                result = documentPathCache.get(path);
            } else {
                result = path.evaluate(rootDocument, rootDocument, configuration).getValue();
                documentPathCache.put(path, result);
            }
        } else {
            result = path.evaluate(contextDocument, rootDocument, configuration).getValue();
        }
        return result;
    }

    public HashMap<Path, Object> documentPathCache() {
        return documentPathCache;
    }

    public HashMap<String, Pattern> compiledPatternCache() {
        return compiledPatternCache;
    }

    @Override
    public Object item() {
        return contextDocument;
    }

    @Override
    public <T> T item(Class<T> clazz) throws MappingException {
        return  configuration().mappingProvider().map(contextDocument, clazz, configuration);
    }

    @Override
    public Object root() {
        return rootDocument;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

}