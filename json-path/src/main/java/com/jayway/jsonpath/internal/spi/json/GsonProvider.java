package com.jayway.jsonpath.internal.spi.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.LazilyParsedNumber;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.ValueCompareException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GsonProvider extends AbstractJsonProvider {

    private static final Logger logger = LoggerFactory.getLogger(GsonProvider.class);

    private static final JsonParser parser = new JsonParser();
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(Number.class, new NumberTypeAdapter()).create();

    static {

        //ConverterFactory.registerConverter();

    }

    public int compare(Object expected, Object providerParsed) throws ValueCompareException {

        JsonElement element = (JsonElement) providerParsed;

        boolean nullish = isNullish(expected);

        if (nullish && !element.isJsonNull()) {
            return -1;
        } else if (!nullish && element.isJsonNull()) {
            return 1;
        } else if (nullish && element.isJsonNull()) {
            return 0;
        }
        if(element.isJsonPrimitive()){
            JsonPrimitive primitive = element.getAsJsonPrimitive();

            if (expected instanceof String && primitive.isString()) {
                return ((String) expected).compareTo(primitive.getAsString());
            } else if (expected instanceof Number && primitive.isNumber()) {
                return new BigDecimal(expected.toString()).compareTo(new BigDecimal(primitive.toString()));
            } else if (expected instanceof String && primitive.isNumber()) {
                return new BigDecimal(expected.toString()).compareTo(new BigDecimal(primitive.toString()));
            } else if (expected instanceof String && primitive.isBoolean()) {
                Boolean e = Boolean.valueOf((String)expected);
                Boolean a = primitive.getAsBoolean();
                return e.compareTo(a);
            } else if (expected instanceof Boolean && primitive.isBoolean()) {
                Boolean e = (Boolean) expected;
                Boolean a = primitive.getAsBoolean();
                return e.compareTo(a);
            }
        }
        logger.debug("Can not compare a {} with a {}", expected.getClass().getName(), providerParsed.getClass().getName());
        throw new ValueCompareException();
    }

    private static boolean isNullish(Object o){
        return (o == null || ((o instanceof String) && ("null".equals(o))));
    }


    public Object unwrap(Object o) {
        return o;
        /*
        if (o == null) {
            return null;
        }
        if (!(o instanceof JsonElement)) {
            return o;
        }

        Object unwrapped = null;

        JsonElement e = (JsonElement) o;

        if (e.isJsonNull()) {
            unwrapped = null;
        } else if (e.isJsonPrimitive()) {

            JsonPrimitive p = e.getAsJsonPrimitive();
            if (p.isString()) {
                unwrapped = p.getAsString();
            } else if (p.isBoolean()) {
                unwrapped = p.getAsBoolean();
            } else if (p.isNumber()) {
                unwrapped = unwrapNumber(p.getAsNumber());
            }
        } else {
            //unwrapped = o;
            if (e.isJsonArray()) {
                JsonArray res = new JsonArray();
                for (JsonElement jsonElement : e.getAsJsonArray()) {
                   if(jsonElement.isJsonPrimitive()){
                       JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
                       if(primitive.isNumber()){
                           res.add(new JsonPrimitive(unwrapNumber(primitive.getAsNumber())));
                       }
                   }
                }
                unwrapped = res;
                //unwrapped = gson.fromJson(e, List.class);
            } else {
                unwrapped = gson.fromJson(e, Map.class);
            }

        }
        return unwrapped;
        */
    }

    private Number unwrapNumber(Number n){
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
                if (bigDecimal.compareTo(new BigDecimal(Float.MAX_VALUE)) <= 0) {
                    unwrapped = bigDecimal.floatValue();
                } else {
                    unwrapped = bigDecimal.doubleValue();
                }
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
    public Object createNull(){
        return JsonNull.INSTANCE;
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

    public boolean isString(Object obj){
        if(obj == null) {
            return false;
        }
        JsonElement element = toJsonElement(obj);
        if(element.isJsonPrimitive()){
            return element.getAsJsonPrimitive().isString();
        }
        return false;
    }

    @Override
    public Object getArrayIndex(Object obj, int idx) {
        return toJsonArray(obj).get(idx);
    }


    @Override
    public Object getMapValue(Object obj, String key) {
        Object o = toJsonObject(obj).get(key);
        if (o == null) {
            return UNDEFINED;
        } else {
            return o;
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
        return (obj instanceof JsonObject) ;
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
        } else if(isMap(obj)){
            return toJsonObject(obj).entrySet().size();
        } else {
            if(obj instanceof JsonElement){
                JsonElement element = toJsonElement(obj);
                if(element.isJsonPrimitive()){
                    return element.toString().length();
                }
            }
        }
        throw new RuntimeException("length operation can not applied to " + obj!=null?obj.getClass().getName():"null");
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


    public static class NumberTypeAdapter
            implements JsonSerializer<Number>, JsonDeserializer<Number>,
            InstanceCreator<Number> {

        public JsonElement serialize(Number src, Type typeOfSrc, JsonSerializationContext
                context) {
            return new JsonPrimitive(src);
        }

        public Number deserialize(JsonElement json, Type typeOfT,
                                  JsonDeserializationContext context)
                throws JsonParseException {

            Number res = null;
            JsonPrimitive jsonPrimitive = json.getAsJsonPrimitive();
            if (jsonPrimitive.isNumber()) {
                Number n = jsonPrimitive.getAsNumber();
                if (n instanceof LazilyParsedNumber) {
                    LazilyParsedNumber lpn = (LazilyParsedNumber) n;
                    BigDecimal bigDecimal = new BigDecimal(lpn.toString());
                    if (bigDecimal.scale() <= 0) {
                        if (bigDecimal.compareTo(new BigDecimal(Integer.MAX_VALUE)) <= 0) {
                            res = bigDecimal.intValue();
                        } else {
                            res = bigDecimal.longValue();
                        }
                    } else {
                        if (bigDecimal.compareTo(new BigDecimal(Float.MAX_VALUE)) <= 0) {
                            res = bigDecimal.floatValue();
                        } else {
                            res = bigDecimal.doubleValue();
                        }
                    }
                }
            } else {
                throw new IllegalStateException("Expected a number field, but was " + json);
            }
            return res;
        }



        public Number createInstance(Type type) {
            return 1L;
        }
    }

}
