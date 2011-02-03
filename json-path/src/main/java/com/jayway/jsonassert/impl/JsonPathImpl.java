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
 * <p/>
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

    public static <T> T jsonPath(Object model, String path) {
        JsonPathImpl p = new JsonPathImpl(model);

        return (T) p.get(path);
    }


    public JsonPathImpl(Object model) {
        this.model = model;
    }


    private void searchFragment(Object visit, PathFragment fragment, PathFragment filter, ReaderContext ctx) {
        ctx.split();
        if (isDocument(visit)) {
            Map<String, Object> document = toDocument(visit);

            for (Map.Entry<String, Object> entry : document.entrySet()) {

                if (entry.getKey().equals(fragment.value())) {

                    if (isArray(entry.getValue()) && filter.isArrayIndex()) {
                        JSONArray array = toArray(entry.getValue());
                        for (int i = 0; i < array.size(); i++) {
                            boolean include = true;

                            if (filter.isArrayIndex() && filter.getArrayIndex() != i) {
                                include = false;
                            }

                            if (include) {
                                ctx.addResult(array.get(i));
                            }
                        }
                    } else {
                        ctx.addResult(entry.getValue());
                    }
                }

                if (isContainer(entry.getValue())) {
                    searchFragment(entry.getValue(), fragment, filter, ctx);
                }
            }
        } else {
            JSONArray array = toArray(visit);

            for (Object arrayItem : array) {
                if (isContainer(arrayItem)) {
                    searchFragment(arrayItem, fragment, filter, ctx);
                }
            }

        }
    }

    private void extractFragment(Object visit, PathFragment fragment, PathFragment criteria, ReaderContext ctx) {
        ctx.split();
        if (isDocument(visit)) {
            Map<String, Object> document = toDocument(visit);

            for (Map.Entry<String, Object> entry : document.entrySet()) {

                if (entry.getKey().equals(fragment.value()) || fragment.isWildcard()) {
                    ctx.addResult(entry.getValue());
                }

                if (!fragment.isWildcard() && isContainer(entry.getValue())) {
                    extractFragment(entry.getValue(), fragment, criteria, ctx);
                }
            }
        } else {
            JSONArray array = toArray(visit);

            for (Object arrayItem : array) {
                if (isContainer(arrayItem)) {
                    extractFragment(arrayItem, fragment, criteria, ctx);
                }
            }
        }
    }

    private void read(Object root, Path path, ReaderContext ctx, boolean strict) {
        if (!path.hasMoreFragments()) {
            ctx.addResult(root);
            return;
        }
        if (path.peek().isWildcard()) {
            PathFragment wildcard = path.poll();
            PathFragment extract = path.hasMoreFragments() ? path.poll() : wildcard;
            PathFragment filter = path.hasMoreFragments() ? path.poll() : wildcard;
            searchFragment(root, extract, filter, ctx);
            return;
        }


        if (isDocument(root)) {
            PathFragment fragment = path.poll();

            if (strict && !toDocument(root).containsKey(fragment.value())) {
                throw new InvalidPathException();
            }

            Object current = toDocument(root).get(fragment.value());

            if (path.hasMoreFragments() && path.peek().isWildcard()) {
                PathFragment wildcard = path.poll();
                PathFragment extract = path.hasMoreFragments() ? path.poll() : wildcard;
                PathFragment filter = path.hasMoreFragments() ? path.poll() : wildcard;
                extractFragment(current, extract, filter, ctx);
                return;
            }
            else if (fragment.isLeaf()) {
                ctx.addResult(current);
            } else {
                read(current, path.clone(), ctx, strict);
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

    public <T> Map<String, T> getMap(String path) {
        return get(path);
    }

    private boolean isContainer(Object obj) {
        return (isArray(obj) || isDocument(obj));
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
