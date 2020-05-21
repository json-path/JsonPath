package com.jayway.jsonpath.spi.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jayway.jsonpath.InvalidJsonException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class FastjsonJsonProvider extends AbstractJsonProvider {

    @Override
    public Object parse(String json) throws InvalidJsonException {
        return JSONObject.parse(json);
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
        try {
            return JSONObject.parseObject(jsonStream, Charset.forName(charset), Object.class);
        } catch (IOException ioe) {
            throw new InvalidJsonException(ioe);
        }
    }

    @Override
    public String toJson(final Object obj) {
        return JSON.toJSONString(obj);
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
    public Object getArrayIndex(final Object obj, final int idx) {
        return toJsonArray(obj).get(idx);
    }

    @Override
    public void setArrayIndex(Object array, int index, Object newValue) {
        if (!isArray(array)) {
            throw new UnsupportedOperationException();
        } else {
            JSONArray arr = toJsonArray(array);
            if (index == arr.size()) {
                arr.add(newValue);
            } else {
                arr.set(index, newValue);
            }
        }
    }

    @Override
    public void setProperty(Object obj, Object key, Object value) {
        if (isMap(obj)) {
            toJSONObject(obj).put(key.toString(), JSON.toJSON(value));
        } else {
            JSONArray array = toJsonArray(obj);
            int index;
            if (key != null) {
                index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            } else {
                index = array.size();
            }

            if (index == array.size()) {
                array.add(JSON.toJSON(value));
            } else {
                array.set(index, JSON.toJSON(value));
            }
        }
    }

    private JSONArray toJsonArray(Object o) {
        return (JSONArray) o;
    }

    private JSONObject toJSONObject(Object o) {
        return (JSONObject) o;
    }
}
