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
package com.jayway.jsonpath.internal;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.EvaluationListener;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.Predicate;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.http.HttpProviderFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.jayway.jsonpath.internal.Utils.notEmpty;
import static com.jayway.jsonpath.internal.Utils.notNull;

public class JsonReader implements ParseContext, DocumentContext {

    private final Configuration configuration;
    private Object json;

    public JsonReader() {
        this(Configuration.defaultConfiguration());
    }

    public JsonReader(Configuration configuration) {
        notNull(configuration, "configuration can not be null");
        this.configuration = configuration;
    }

    private JsonReader(Object json, Configuration configuration) {
        notNull(json, "json can not be null");
        notNull(configuration, "configuration can not be null");
        this.configuration = configuration;
        this.json = json;
    }

    //------------------------------------------------
    //
    // ParseContext impl
    //
    //------------------------------------------------
    @Override
    public DocumentContext parse(Object json) {
        notNull(json, "json object can not be null");
        this.json = json;
        return this;
    }

    @Override
    public DocumentContext parse(String json) {
        notEmpty(json, "json string can not be null or empty");
        this.json = configuration.jsonProvider().parse(json);
        return this;
    }

    @Override
    public DocumentContext parse(InputStream json) {
        return parse(json, "UTF-8");
    }

    @Override
    public DocumentContext parse(InputStream json, String charset) {
        notNull(json, "json input stream can not be null");
        notNull(json, "charset can not be null");
        try {
            this.json = configuration.jsonProvider().parse(json, charset);
            return this;
        } finally {
            Utils.closeQuietly(json);
        }
    }

    @Override
    public DocumentContext parse(File json) throws IOException {
        notNull(json, "json file can not be null");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(json);
            parse(fis);
        } finally {
            Utils.closeQuietly(fis);
        }
        return this;
    }

    @Override
    public DocumentContext parse(URL json) throws IOException {
        notNull(json, "json url can not be null");
        InputStream is = HttpProviderFactory.getProvider().get(json);
        return parse(is);
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    //------------------------------------------------
    //
    // ReadContext impl
    //
    //------------------------------------------------
    @Override
    public Object json() {
        return json;
    }

    @Override
    public <T> T read(String path, Predicate... filters) {
        notEmpty(path, "path can not be null or empty");
        return read(JsonPath.compile(path, filters));
    }

    @Override
    public <T> T read(String path, Class<T> type, Predicate... filters) {
        return convert(read(path, filters), type, configuration);
    }

    @Override
    public <T> T read(JsonPath path) {
        notNull(path, "path can not be null");
        return path.read(json, configuration);
    }

    @Override
    public <T> T read(JsonPath path, Class<T> type) {
        return convert(read(path), type, configuration);
    }

    public ReadContext limit(int maxResults){
        return withListeners(new LimitingEvaluationListener(maxResults));
    }

    public ReadContext withListeners(EvaluationListener... listener){
        return new JsonReader(json, configuration.setEvaluationListeners(listener));
    }


    private <T> T convert(Object obj, Class<T> targetType, Configuration configuration){
        return configuration.mappingProvider().map(obj, targetType, configuration);
    }

    @Override
    public DocumentContext set(String path, Object newValue, Predicate... filters) {
        Object modifiedJson = JsonPath.compile(path, filters).set(json, newValue, configuration);
        return new JsonReader(modifiedJson, configuration);
    }

    @Override
    public DocumentContext delete(String path, Predicate... filters) {
        Object modifiedJson = JsonPath.compile(path, filters).delete(json, configuration);
        return new JsonReader(modifiedJson, configuration);
    }

    @Override
    public DocumentContext add(String path, Object value, Predicate... filters){
        Object modifiedJson = JsonPath.compile(path, filters).add(json, value, configuration);
        return new JsonReader(modifiedJson, configuration);
    }

    @Override
    public DocumentContext put(String path, String key, Object value, Predicate... filters){
        Object modifiedJson = JsonPath.compile(path, filters).put(json, key, value, configuration);
        return new JsonReader(modifiedJson, configuration);
    }

    private final class LimitingEvaluationListener implements EvaluationListener {

        final int limit;

        private LimitingEvaluationListener(int limit) {
            this.limit = limit;
        }


        @Override
        public EvaluationContinuation resultFound(FoundResult found) {
            if(found.index() == limit - 1){
                return EvaluationContinuation.ABORT;
            } else {
                return EvaluationContinuation.CONTINUE;
            }
        }
    }
}
