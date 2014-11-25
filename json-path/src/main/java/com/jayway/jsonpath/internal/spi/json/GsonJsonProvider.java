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
package com.jayway.jsonpath.internal.spi.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GsonJsonProvider extends AbstractJsonProvider {

    private static final Logger logger = LoggerFactory.getLogger(GsonJsonProvider.class);

    private static final JsonParser parser = new JsonParser();
    private static final Gson gson = new GsonBuilder().create();


    public Object unwrap(Object o) {

        if (o == null) {
            return null;
        }
        if (!(o instanceof JsonElement)) {
            return o;
        }

        JsonElement e = (JsonElement) o;

        if (e.isJsonNull()) {
            return null;
        } else if (e.isJsonPrimitive()) {

            JsonPrimitive p = e.getAsJsonPrimitive();
            if (p.isString()) {
                return p.getAsString();
            } else if (p.isBoolean()) {
                return p.getAsBoolean();
            } else if (p.isNumber()) {
                return unwrapNumber(p.getAsNumber());
            }
        }
        return o;
    }

    private static Number unwrapNumber(Number n) {
        Number unwrapped;

        if (n instanceof LazilyParsedNumber) {
            LazilyParsedNumber lpn = (LazilyParsedNumber) n;
            BigDecimal bigDecimal = new BigDecimal(lpn.toString());
            if (bigDecimal.scale() <= 0) {
                if (bigDecimal.compareTo(new BigDecimal(Integer.MAX_VALUE)) <= 0) {
                    unwrapped = bigDecimal.intValue();
                } else {
                    unwrapped = bigDecimal.longValue();
                }
            } else {
                unwrapped = bigDecimal.doubleValue();
            }
        } else {
            unwrapped = n;
        }
        return unwrapped;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        return parser.parse(json);
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {

        try {
            return parser.parse(new InputStreamReader(jsonStream, charset));
        } catch (UnsupportedEncodingException e) {
            throw new JsonPathException(e);
        }
    }

    @Override
    public String toJson(Object obj) {
        return obj.toString();
    }

    @Override
    public Object createArray() {
        return new JsonArray();
    }

    @Override
    public Object createMap() {
        return new JsonObject();
    }

    @Override
    public boolean isArray(Object obj) {
        return (obj instanceof JsonArray || obj instanceof List);
    }

    @Override
    public Object getArrayIndex(Object obj, int idx) {
        return toJsonArray(obj).get(idx);
    }

    @Override
    public void setArrayIndex(Object array, int index, Object newValue) {
        if (!isArray(array)) {
            throw new UnsupportedOperationException();
        } else {
            toJsonArray(array).set (index, createJsonElement(newValue));
        }
    }

    @Override
    public Object getMapValue(Object obj, String key) {
        JsonObject jsonObject = toJsonObject(obj);
        Object o = jsonObject.get(key);
        if (!jsonObject.has(key)) {
            return UNDEFINED;
        } else {
            return unwrap(o);
        }
    }

    @Override
    public void setProperty(Object obj, Object key, Object value) {
        if (isMap(obj))
            toJsonObject(obj).add(key.toString(), createJsonElement(value));
        else {
            JsonArray array = toJsonArray(obj);
            int index;
            if (key != null) {
                index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            } else {
                index = array.size();
            }
            if (index == array.size()) {
                array.add(createJsonElement(value));
            } else {
                array.set(index, createJsonElement(value));
            }
        }
    }



    @SuppressWarnings("unchecked")
    public void removeProperty(Object obj, Object key) {
        if (isMap(obj))
            toJsonObject(obj).remove(key.toString());
        else {
            JsonArray array = toJsonArray(obj);
            int index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            array.remove(index);
        }
    }

    @Override
    public boolean isMap(Object obj) {
        //return (obj instanceof JsonObject || obj instanceof Map);
        return (obj instanceof JsonObject);
    }

    @Override
    public Collection<String> getPropertyKeys(Object obj) {
        List<String> keys = new ArrayList<String>();
        for (Map.Entry<String, JsonElement> entry : toJsonObject(obj).entrySet()) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    @Override
    public int length(Object obj) {
        if (isArray(obj)) {
            return toJsonArray(obj).size();
        } else if (isMap(obj)) {
            return toJsonObject(obj).entrySet().size();
        } else {
            if (obj instanceof JsonElement) {
                JsonElement element = toJsonElement(obj);
                if (element.isJsonPrimitive()) {
                    return element.toString().length();
                }
            }
        }
        throw new JsonPathException("length operation can not applied to " + obj != null ? obj.getClass().getName() : "null");
    }

    @Override
    public Iterable<?> toIterable(Object obj) {
        if (isArray(obj)) {
            JsonArray arr = toJsonArray(obj);
            List<Object> values = new ArrayList<Object>(arr.size());
            for (Object o : arr) {
                values.add(unwrap(o));
            }
            return values;
        } else {
            JsonObject jsonObject = toJsonObject(obj);
            List<Object> values = new ArrayList<Object>();
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                values.add(unwrap(entry.getValue()));
            }
            return values;
        }
    }

    private JsonElement createJsonElement(Object o) {
        return gson.toJsonTree(o);
    }

    private JsonArray toJsonArray(Object o) {
        return (JsonArray) o;
    }

    private JsonObject toJsonObject(Object o) {
        return (JsonObject) o;
    }

    private JsonElement toJsonElement(Object o) {
        return (JsonElement) o;
    }
}
