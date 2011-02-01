package com.jayway.jsonassert.impl;

import com.jayway.jsonassert.InvalidPathException;
import com.jayway.jsonassert.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * User: kalle stenflo
 * Date: 2/1/11
 * Time: 8:45 AM
 *
 * http://goessner.net/articles/JsonPath/
 */
public class JsonPathImpl implements JsonPath {

    private static final JSONParser JSON_PARSER = new JSONParser();

    private Object model;

    public static synchronized JsonPath parse(Reader reader) throws java.text.ParseException, IOException {
        try {
            return new JsonPathImpl(JSON_PARSER.parse(reader));
        } catch (IOException e) {
            throw e;
        } catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), e.getPosition());
        }
    }

    public static synchronized JsonPath parse(String jsonDoc) throws java.text.ParseException {
        try {
            return new JsonPathImpl(JSON_PARSER.parse(jsonDoc));
        } catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), e.getPosition());
        }
    }

    public JsonPathImpl(Object model) {
        this.model = model;
    }

    private void read(Object root, Path path, ReaderContext ctx, boolean strict) {
        if (!path.hasMoreFragments()) {
            ctx.addResult(root);
            return;
        }
        if (isDocument(root)) {
            PathFragment fragment = path.poll();

            if(strict && !toDocument(root).containsKey(fragment.value())){
                throw new InvalidPathException();
            }

            Object extracted = toDocument(root).get(fragment.value());

            if (fragment.isLeaf()) {
                ctx.addResult(extracted);
            } else {
                read(extracted, path.clone(), ctx, strict);
            }
        } else {
            PathFragment fragment = path.poll();

            if (fragment.isArrayIndex()) {
                Object extracted = toArray(root).get(fragment.getArrayIndex());

                read(extracted, path.clone(), ctx, strict);

            } else if (fragment.isArrayWildcard()) {
                ctx.split();

                for (Object extracted : toArray(root)) {
                    read(extracted, path.clone(), ctx, strict);
                }
            }
        }
    }

    private static class ReaderContext {
        private JSONArray result = new JSONArray();
        private boolean pathSplit = false;

        private void addResult(Object obj) {
            result.add(obj);
        }

        private void split() {
            pathSplit = true;
        }

        private boolean isPathSplit() {
            return pathSplit;
        }
    }

    private <T> T get(String jsonPath, boolean strict) {
        Path path = new Path(jsonPath);

        ReaderContext ctx = new ReaderContext();

        read(model, path.clone(), ctx, strict);

        if (ctx.isPathSplit()) {
            return (T) ctx.result;
        } else {
            return (T) ctx.result.get(0);
        }
    }

    public JsonPath getReader(String path) {

        Object subModel = get(path);

        if (!isArray(subModel) && !isDocument(subModel)) {
            throw new InvalidPathException();
        }
        return new JsonPathImpl(subModel);
    }

    public boolean hasJsonPath(String path) {
        boolean hasPath = true;
        try {
            get(path, true);
        } catch (Exception e) {
            hasPath = false;
        }
        return hasPath;
    }

    public boolean isNull(String path) {
        return (get(path) == null);
    }

    public <T> T get(String jsonPath) {
        return (T) get(jsonPath, false);
    }

    public String getString(String path) {
        return get(path);
    }

    public Long getLong(String path) {
        return get(path);
    }

    public Double getDouble(String path) {
        return get(path);
    }

    public Boolean getBoolean(String path) {
        return get(path);
    }

    public <T> List<T> getList(String path) {
        return get(path);
    }

    public Map<String, Object> getMap(String path) {
        return get(path);
    }

    private boolean isArray(Object obj) {
        return (obj instanceof JSONArray);
    }

    private boolean isDocument(Object obj) {
        return (obj instanceof JSONObject);
    }

    private JSONArray toArray(Object array) {
        return (JSONArray) array;
    }

    private JSONObject toDocument(Object document) {
        return (JSONObject) document;
    }
}
