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
import com.jayway.jsonpath.EvaluationListener;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.EvaluationAbortException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.spi.json.JsonProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.jayway.jsonpath.internal.Utils.notNull;

/**
 * I add two variables leavePathResult and leaveResultIndex to implement the root-leave path function
 * And two methods getLeavePath and getLeavePathList
 *
 * Created by XiaoLing12138 on 05/21/2022.
 */
public class EvaluationContextImpl implements EvaluationContext {

    private static final EvaluationAbortException ABORT_EVALUATION = new EvaluationAbortException();

    private final Configuration configuration;
    private final Object valueResult;
    private final Object pathResult;
    private final Object leavePathResult;
    private final Path path;
    private final Object rootDocument;
    private final List<PathRef> updateOperations;
    private final HashMap<Path, Object> documentEvalCache = new HashMap<Path, Object>();
    private final boolean forUpdate;
    private final boolean suppressExceptions;
    private int resultIndex = 0;
    private int leaveResultIndex = 0;


    public RootPathToken getRoot(){
        return ((CompiledPath) path).getRoot();
    }

    public EvaluationContextImpl(Path path, Object rootDocument, Configuration configuration, boolean forUpdate) {
        notNull(path, "path can not be null");
        notNull(rootDocument, "root can not be null");
        notNull(configuration, "configuration can not be null");
        this.forUpdate = forUpdate;
        this.path = path;
        this.rootDocument = rootDocument;
        this.configuration = configuration;
        this.valueResult = configuration.jsonProvider().createArray();
        this.pathResult = configuration.jsonProvider().createArray();
        this.leavePathResult = configuration.jsonProvider().createArray();
        this.updateOperations = new ArrayList<>();
        this.suppressExceptions = configuration.containsOption(Option.SUPPRESS_EXCEPTIONS);
    }

    public HashMap<Path, Object> documentEvalCache() {
        return documentEvalCache;
    }

    public boolean forUpdate(){
        return forUpdate;
    }

    /**
     * Here we implement the leavePathResult function.
     * It will check whether it is a leave node
     * If it is a leave node, it will be added into leavePathResult
     * Otherwise, ignore.
     *
     * Code is from "char bracket" to "leaveResultIndex++;"
     * Created by XiaoLing12138 on 05/21/2022.
     */
    public void addResult(String path, PathRef operation, Object model) {

        if(forUpdate) {
            updateOperations.add(operation);
        }

        configuration.jsonProvider().setArrayIndex(valueResult, resultIndex, model);
        configuration.jsonProvider().setArrayIndex(pathResult, resultIndex, path);

        char bracket = model.toString().trim().charAt(0);
        if (bracket != '[' && bracket != '{') {
            configuration.jsonProvider().setArrayIndex(leavePathResult, leaveResultIndex, path);
            leaveResultIndex++;
        }

        resultIndex++;
        if(!configuration().getEvaluationListeners().isEmpty()){
            int idx = resultIndex - 1;
            for (EvaluationListener listener : configuration().getEvaluationListeners()) {
                EvaluationListener.EvaluationContinuation continuation = listener.resultFound(new FoundResultImpl(idx, path, model));
                if(EvaluationListener.EvaluationContinuation.ABORT == continuation){
                    throw ABORT_EVALUATION;
                }
            }
        }
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

    public Collection<PathRef> updateOperations(){

        Collections.sort(updateOperations);

        return Collections.unmodifiableCollection(updateOperations);
    }


    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        return getValue(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(boolean unwrap) {
        if (path.isDefinite()) {
            if(resultIndex == 0) {
                if (suppressExceptions) {
                    return null;
                }
                throw new PathNotFoundException("No results for path: " + path.toString());
            }
            int len = jsonProvider().length(valueResult);
            Object value = (len > 0) ? jsonProvider().getArrayIndex(valueResult, len-1) : null;
            if (value != null && unwrap){
              value = jsonProvider().unwrap(value);
            }
            return (T) value;
        }
        return (T)valueResult;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getPath() {
        if(resultIndex == 0) {
            if (suppressExceptions) {
                return null;
            }
            throw new PathNotFoundException("No results for path: " + path.toString());
        }
        return (T)pathResult;
    }

    /**
     * This will return the root-leave path list
     * If it is empty, the user may use path like $..['book']
     * This is not a root-leave path search, and it will return as getPath
     * Which works the same as AS_PATH_LIST
     *
     * Created by XiaoLing12138 on 05/21/2022.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getLeavePath() {
        if(leaveResultIndex == 0) {
            return getPath();
        }
        return (T)leavePathResult;
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

    /**
     * This will return the root-leave path list
     * If it is empty, the user may use path like $..['book']
     * This is not a root-leave path search, and it will return as getPathList
     * Which works the same as AS_PATH_LIST
     *
     * Created by XiaoLing12138 on 05/21/2022.
     */
    @Override
    public List<String> getLeavePathList() {
        List<String> res = new ArrayList<>();
        if(leaveResultIndex > 0) {
            Iterable<?> objects = configuration.jsonProvider().toIterable(leavePathResult);
            for (Object o : objects) {
                res.add((String)o);
            }
        } else {
            if (resultIndex > 0) {
                Iterable<?> objects = configuration.jsonProvider().toIterable(pathResult);
                for (Object o : objects) {
                    res.add((String)o);
                }
            }
        }
        return res;
    }

    private static class FoundResultImpl implements EvaluationListener.FoundResult {

        private final int index;
        private final String path;
        private final Object result;

        private FoundResultImpl(int index, String path, Object result) {
            this.index = index;
            this.path = path;
            this.result = result;
        }

        @Override
        public int index() {
            return index;
        }

        @Override
        public String path() {
            return path;
        }

        @Override
        public Object result() {
            return result;
        }
    }

}
