package com.jayway.jsonpath.internal.spi.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LazilyParsedNumber;
import com.jayway.jsonpath.InvalidJsonException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GsonProvider extends AbstractJsonProvider {

    private static final JsonParser parser = new JsonParser();
    private static final Gson gson = new Gson();

    public Object unwrap(Object o){
        if(o == null){
            return null;
        }
        if(!(o instanceof JsonElement)){
            return o;
        }

        Object unwrapped = null;

        JsonElement e = (JsonElement) o;

        if(e.isJsonNull()) {
            unwrapped = null;
        } else if(e.isJsonPrimitive()){

            JsonPrimitive p = e.getAsJsonPrimitive();
            if(p.isString()){
                unwrapped = p.getAsString();
            } else if(p.isBoolean()){
                unwrapped = p.getAsBoolean();
            } else if(p.isNumber()){
                Number n = p.getAsNumber();
                if(n instanceof LazilyParsedNumber){
                    LazilyParsedNumber lpn = (LazilyParsedNumber) n;
                    BigDecimal bigDecimal = new BigDecimal(lpn.toString());
                    if(bigDecimal.scale() <= 0){
                        if(bigDecimal.compareTo(new BigDecimal(Integer.MAX_VALUE)) <= 0){
                            unwrapped = bigDecimal.intValue();
                        } else {
                            unwrapped = bigDecimal.longValue();
                        }
                    } else {
                        if(bigDecimal.compareTo(new BigDecimal(Float.MAX_VALUE)) <= 0){
                            unwrapped = bigDecimal.floatValue();
                        } else {
                            unwrapped = bigDecimal.doubleValue();
                        }
                    }
                } else {
                    unwrapped = n;
                }
            }
        } else {
            unwrapped = o;
        }
        return unwrapped;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        return parser.parse(json);
    }

    @Override
    public Object parse(Reader jsonReader) throws InvalidJsonException {
        return parser.parse(jsonReader);
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
    public Object createMap() {
        return new JsonObject();
    }

    @Override
    public Object createArray() {
        return new JsonArray();
    }

    @Override
    public boolean isArray(Object obj) {
        return (obj instanceof JsonArray);
    }

    @Override
    public Object getArrayIndex(Object obj, int idx) {
        return toJsonArray(obj).get(idx);
    }


    @Override
    public Object getMapValue(Object obj, String key) {
        Object o = toJsonObject(obj).get(key);
        if(o == null){
            return UNDEFINED;
        } else {
            return o;
        }
    }

    @Override
    public void setProperty(Object obj, Object key, Object value) {
        if (isMap(obj))
            toJsonObject(obj).add(key.toString(), toJsonElement(value));
        else {
            JsonArray array = toJsonArray(obj);
            int index;
            if (key != null) {
                index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            } else {
                index = array.size();
            }
            if(index == array.size()){
                array.add(toJsonElement(value));
            } else {
                array.set(index, toJsonElement(value));
            }
        }
    }

    @Override
    public boolean isMap(Object obj) {
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
        } else {
            return toJsonObject(obj).entrySet().size();
        }
    }

    @Override
    public Iterable<?> toIterable(Object obj) {
        if (isArray(obj)) {
            return toJsonArray(obj);
        } else {
            List<JsonElement> values = new ArrayList<JsonElement>();
            JsonObject jsonObject = toJsonObject(obj);
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                values.add(entry.getValue());
            }
            return values;
        }
    }

    private JsonElement toJsonElement(Object o){
        return gson.toJsonTree(o);
    }

    private JsonArray toJsonArray(Object o){
        return (JsonArray) o;
    }

    private JsonObject toJsonObject(Object o){
        return (JsonObject) o;
    }
}
