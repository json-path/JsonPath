package com.jayway.jsonpath.spi.json;

import org.json.JSONObject;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPathException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonOrgJsonProvider extends AbstractJsonProvider {

    @Override
    public Object parse(String json) throws InvalidJsonException {
        try {
            return new JSONTokener(json).nextValue();
        } catch (JSONException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {

        try {
            return new JSONTokener(new InputStreamReader(jsonStream, charset)).nextValue();
        } catch (UnsupportedEncodingException e) {
            throw new JsonPathException(e);
        } catch (JSONException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object unwrap(Object obj) {
        if(obj == JSONObject.NULL){
            return null;
        }
        return obj;
    }

    @Override
    public String toJson(Object obj) {
        return obj.toString();
    }

    @Override
    public Object createArray() {
        return new JSONArray();
    }

    @Override
    public Object createMap() {
        return new JSONObject();
    }

    @Override
    public boolean isArray(Object obj) {
        return (obj instanceof JSONArray || obj instanceof List);
    }

    @Override
    public Object getArrayIndex(Object obj, int idx) {
        try {
            return toJsonArray(obj).get(idx);
        } catch (JSONException e) {
            throw new JsonPathException(e);
        }
    }

    @Override
    public void setArrayIndex(Object array, int index, Object newValue) {
        try {
            if (!isArray(array)) {
                throw new UnsupportedOperationException();
            } else {
                toJsonArray(array).put(index, createJsonElement(newValue));
            }
        } catch (JSONException e) {
            throw new JsonPathException(e);
        }
    }

    @Override
    public Object getMapValue(Object obj, String key) {
        try {
            JSONObject jsonObject = toJsonObject(obj);
            Object o = jsonObject.opt(key);
            if (o == null) {
                return UNDEFINED;
            } else {
                return unwrap(o);
            }
        } catch (JSONException e) {
            throw new JsonPathException(e);
        }
    }

    @Override
    public void setProperty(Object obj, Object key, Object value) {
        try {
            if (isMap(obj))
                toJsonObject(obj).put(key.toString(), createJsonElement(value));
            else {
                JSONArray array = toJsonArray(obj);
                int index;
                if (key != null) {
                    index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
                } else {
                    index = array.length();
                }
                if (index == array.length()) {
                    array.put(createJsonElement(value));
                } else {
                    array.put(index, createJsonElement(value));
                }
            }
        } catch (JSONException e) {
            throw new JsonPathException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void removeProperty(Object obj, Object key) {
        if (isMap(obj))
            toJsonObject(obj).remove(key.toString());
        else {
            JSONArray array = toJsonArray(obj);
            int index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            array.remove(index);
        }
    }

    @Override
    public boolean isMap(Object obj) {
        return (obj instanceof JSONObject);
    }

    @Override
    public Collection<String> getPropertyKeys(Object obj) {
        JSONObject jsonObject = toJsonObject(obj);
        List<String> keys = new ArrayList<String>();
        try {
            for (int i = 0; i < jsonObject.names().length(); i++) {
                String key = (String) jsonObject.names().get(i);
                keys.add(key);

            }
            return keys;
        } catch (JSONException e) {
            throw new JsonPathException(e);
        }
    }

    @Override
    public int length(Object obj) {
        if (isArray(obj)) {
            return toJsonArray(obj).length();
        } else if (isMap(obj)) {
            return toJsonObject(obj).length();
        } else {
            if (obj instanceof String) {
                return ((String) obj).length();
            }
        }
        throw new JsonPathException("length operation can not applied to " + (obj != null ? obj.getClass().getName()
                : "null"));
    }

    @Override
    public Iterable<?> toIterable(Object obj) {
        try {
            if (isArray(obj)) {
                JSONArray arr = toJsonArray(obj);
                List<Object> values = new ArrayList<Object>(arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    values.add(unwrap(arr.get(i)));
                }
                return values;
            } else {
                JSONObject jsonObject = toJsonObject(obj);
                List<Object> values = new ArrayList<Object>();

                for (int i = 0; i < jsonObject.names().length(); i++) {
                    String key = (String) jsonObject.names().get(i);
                    Object val = jsonObject.get(key);
                    values.add(unwrap(val));

                }

                return values;
            }
        } catch (JSONException e) {
            throw new JsonPathException(e);
        }
    }

    private Object createJsonElement(Object o) {
        return o;
    }

    private JSONArray toJsonArray(Object o) {
        return (JSONArray) o;
    }

    private JSONObject toJsonObject(Object o) {
        return (JSONObject) o;
    }

}
