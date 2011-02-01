package com.jayway.jsonassert.impl;

import com.jayway.jsonassert.InvalidPathException;
import com.jayway.jsonassert.JsonPath;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.Validate.notNull;

/**
 * User: kalle stenflo
 * Date: 1/20/11
 * Time: 4:27 PM
 */
public class JsonReaderImpl implements JsonPath {


    private static final JSONParser JSON_PARSER = new JSONParser();

    private Object root;
    private String currentPath;


    public static synchronized JsonPath parse(Reader reader) throws java.text.ParseException, IOException {
        try {
            return new JsonReaderImpl(JSON_PARSER.parse(reader));
        } catch (IOException e) {
            throw e;
        } catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), e.getPosition());
        }
    }

    public static synchronized JsonPath parse(String jsonDoc) throws java.text.ParseException {
        try {
            return new JsonReaderImpl(JSON_PARSER.parse(jsonDoc));
        } catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), e.getPosition());
        }
    }

    private JsonReaderImpl(Object root) {
        notNull(root, "root object can not be null");
        this.root = root;
    }

    /**
     * {@inheritDoc}
     */
    public JsonPath getReader(String path) {

        Object jsonObject = get(path);

        if (!isArray(jsonObject) && !isDocument(jsonObject)) {
            throw new InvalidPathException("path points to a leaf that is not a JSON document or Array");
        }

        return new JsonReaderImpl(jsonObject);
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasJsonPath(String path) {
        boolean contains = true;
        try {
            get(path);
        } catch (InvalidPathException e) {
            contains = false;
        }
        return contains;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isNull(String path) {
        return (null == get(path));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String path) {
        return (T) getByPath(Object.class, path);
    }

    /**
     * {@inheritDoc}
     */
    public String getString(String path) {
        return getByPath(String.class, path);
    }

    /**
     * {@inheritDoc}
     */
    public Long getLong(String path) {
        return getByPath(Long.class, path);
    }

    /**
     * {@inheritDoc}
     */
    public Double getDouble(String path) {
        return getByPath(Double.class, path);
    }

    /**
     * {@inheritDoc}
     */
    public Boolean getBoolean(String path) {
        return getByPath(Boolean.class, path);
    }

    /**
     * {@inheritDoc}
     */
    public <T> List<T> getList(String path) {
        return getByPath(List.class, path);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, Object> getMap(String path) {
        return getByPath(Map.class, path);
    }


    //------------------------------------------------------------
    //
    // private methods
    //
    //------------------------------------------------------------

    /*
    private <T> T getByPath(Class<T> clazz, String stringPath) {
        currentPath = "";
        Object current = this.root;
        JSONPath path = new JSONPath(stringPath);

        while (path.hasMoreFragments()) {

            JSONPathFragment fragment = path.poll();

            currentPath = fragment.appendToPath(currentPath);

            if (fragment.isArrayIndex()) {
                current = toArray(current).get(fragment.getArrayIndex());
            } else if (fragment.isArrayWildcard()) {
                current = getContainerValue(current, path.poll());
            } else {
                current = getContainerValue(current, fragment);
            }
        }
        return clazz.cast(current);
    }
    */

    ///*
    private <T> T getByPath(Class<T> clazz, String stringPath) {
        currentPath = "";
        Object current = this.root;
        Path path = new Path(stringPath);

        while (path.hasMoreFragments()) {

            PathFragment fragment = path.poll();

            currentPath = fragment.appendToPath(currentPath);

            if (fragment.isArrayIndex()) {
                current = toArray(current).get(fragment.getArrayIndex());
            } else if (fragment.isArrayWildcard()) {
                current = getContainerValue(current, path.poll());
            } else {

                System.out.println("FRAGMENT " + fragment.toString());

                current = getContainerValue(current, fragment);

                if (isArray(current) && path.hasMoreFragments() && !path.peek().isArrayIndex() && !path.peek().isArrayWildcard()) {

                    JSONArray array = new JSONArray();

                    for (Object o : toArray(current)) {
                        String newPath = path.toString();

                        System.out.println("NEW PATH " + newPath);

                        if (isDocument(o) || isArray(o)) {

                            JsonReaderImpl sub = new JsonReaderImpl(o);

                            Object o1 = sub.get(newPath);
                            //if(o instanceof Collection){
                                array.add(o1);
                            //}
                            //else {
                            //    array.addAll(l)
                            //}


                        } else {
                            System.out.println("hhhhhhh");
                            array.add(o);
                        }
                    }
                    current = array;
                    break;
                }
            }
        }
        return clazz.cast(current);
    }

    //*/
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

    /**
     * Extracts a field from a given container. If the given container
     * is an Array the field specified represents a field in the objects
     * contained in the array. Values from all instances of this field
     * will be returned in a List
     *
     * @param container a json document or array
     * @param fragment  the field to extract from the document alt. the documents contained in the array
     * @return a single field value or a List of fields
     */
    private Object getContainerValue(Object container, PathFragment fragment) {
        Object result;

        if (container instanceof JSONArray) {
            List<Object> list = new LinkedList<Object>();
            for (Object doc : toArray(container)) {
                list.add(getContainerValue(doc, fragment));
            }
            result = list;

        } else if (container instanceof JSONObject) {
            JSONObject document = toDocument(container);

            if (!document.containsKey(fragment.value())) {
                throw new InvalidPathException("Invalid path element: " + currentPath + " <==");
            }

            result = document.get(fragment.value());
        } else {
            throw new InvalidPathException("Invalid path element: " + currentPath + " <==");
        }
        return result;
    }
}
