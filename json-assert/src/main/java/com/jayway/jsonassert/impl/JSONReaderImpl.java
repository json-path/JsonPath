package com.jayway.jsonassert.impl;

import com.jayway.jsonassert.InvalidPathException;
import com.jayway.jsonassert.JSONReader;
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
public class JSONReaderImpl implements JSONReader {


    private static final JSONParser JSON_PARSER = new JSONParser();

    private Object root;


    public static synchronized JSONReader parse(Reader reader) throws java.text.ParseException, IOException {
        try {
            return new JSONReaderImpl(JSON_PARSER.parse(reader));
        } catch (IOException e) {
            throw e;
        } catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), e.getPosition());
        }
    }

    public static synchronized JSONReader parse(String jsonDoc) throws java.text.ParseException {
        try {
            return new JSONReaderImpl(JSON_PARSER.parse(jsonDoc));
        } catch (ParseException e) {
            throw new java.text.ParseException(e.getMessage(), e.getPosition());
        }
    }

    private JSONReaderImpl(Object root) {
        notNull(root, "root object can not be null");
        this.root = root;
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
    public Object get(String path) {
        return getByPath(Object.class, path);
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
    public Map getMap(String path) {
        return getByPath(Map.class, path);
    }


    //------------------------------------------------------------
    //
    // private methods
    //
    //------------------------------------------------------------


    private <T> T getByPath(Class<T> clazz, String stringPath) {
        Object current = this.root;
        JSONPath path = new JSONPath(stringPath);

        while (path.hasMoreFragments()) {
            JSONPathFragment fragment = path.nextFragment();
            if (fragment.isArrayIndex()) {
                current = getArray(current).get(fragment.getArrayIndex());
            } else if (fragment.isArrayWildcard()) {
                current = getContainerValue(current, path.nextFragment().fragment());
            } else {
                current = getContainerValue(current, fragment.fragment());
            }
        }
        return clazz.cast(current);
    }

    private JSONArray getArray(Object array) {
        return (JSONArray) array;
    }

    private JSONObject getDocument(Object document) {
        return (JSONObject) document;
    }

    /**
     * Extracts a field from a given container. If the given container
     * is an Array the field specified represents a field in the objects
     * contained in the array. Values from all instances of this field
     * will be returned in a List
     *
     * @param container a json document or array
     * @param field     the field to extract from the document alt. the documents contained in the array
     * @return a single field value or a List of fields
     */
    private Object getContainerValue(Object container, Object field) {
        Object result;

        if (container instanceof JSONArray) {
            List list = new LinkedList();
            for (Object doc : getArray(container)) {
                list.add(getContainerValue(doc, field));
            }
            result = list;

        } else if (container instanceof JSONObject) {
            JSONObject document = getDocument(container);

            if (!document.containsKey(field)) {
                throw new InvalidPathException();
            }

            result = document.get(field);
        } else {
            throw new UnsupportedOperationException("can not get value from " + container.getClass().getName());
        }
        //notNull(result, "invalid path: " + field);

        return result;
    }
}
