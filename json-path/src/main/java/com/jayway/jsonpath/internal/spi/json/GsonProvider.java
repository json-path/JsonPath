package com.jayway.jsonpath.internal.spi.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import com.jayway.jsonpath.InvalidJsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GsonProvider extends AbstractJsonProvider {

    private static final Logger logger = LoggerFactory.getLogger(GsonProvider.class);

    private static final JsonParser parser = new JsonParser();
    private static final Gson gson = new GsonBuilder().create();


    public static Object unwrap(Object o) {

        System.out.println("unwrap " + o);
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
    public Object parse(InputStream jsonStream) throws InvalidJsonException {
        return parser.parse(new InputStreamReader(jsonStream));
    }

    @Override
    public String toJson(Object obj) {
        return obj.toString();
    }


    @Override
    public Object createNull() {
        return JsonNull.INSTANCE;
    }

    @Override
    public Object createArray() {
        return new JsonArray();
    }

    @Override
    public boolean isArray(Object obj) {
        return (obj instanceof JsonArray || obj instanceof List);
    }

    @Override
    public Object getArrayIndex(Object obj, int idx) {
        System.out.println("getArrayIndex " + obj + " (" + obj.getClass() + ")");
        return unwrap(toJsonArray(obj).get(idx));
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

    @Override
    public boolean isMap(Object obj) {
        System.out.println("isMap " + obj + " (" + obj.getClass() + ")");
        //return (obj instanceof JsonObject || obj instanceof Map);
        return (obj instanceof JsonObject);
    }

    @Override
    public Collection<String> getPropertyKeys(Object obj) {
        System.out.println("getPropertyKeys " + obj);
        List<String> keys = new ArrayList<String>();
        for (Map.Entry<String, JsonElement> entry : toJsonObject(obj).entrySet()) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    @Override
    public int length(Object obj) {
        System.out.println("length " + obj);
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
        throw new RuntimeException("length operation can not applied to " + obj != null ? obj.getClass().getName() : "null");
    }

    @Override
    public Iterable<?> toIterable(Object obj) {
        System.out.println("toIterable " + obj + " (" + obj.getClass() + ")");
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
