package com.jayway.jsonpath.spi.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.JsonPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class JacksonJsonNodeJsonProvider extends AbstractJsonProvider {

    private static final Logger logger = LoggerFactory.getLogger(JacksonJsonProvider.class);

    private static final ObjectMapper defaultObjectMapper = new ObjectMapper();

    protected ObjectMapper objectMapper;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * Initialize the JacksonTreeJsonProvider with the default ObjectMapper and ObjectReader
     */
    public JacksonJsonNodeJsonProvider() {
        this(defaultObjectMapper);
    }

    /**
     * Initialize the JacksonTreeJsonProvider with a custom ObjectMapper and ObjectReader.
     *
     * @param objectMapper the ObjectMapper to use
     */
    public JacksonJsonNodeJsonProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        try {
            return objectMapper.readTree(json);
        } catch (IOException e) {
            logger.debug("Invalid JSON: \n" + json);
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream, String charset) throws InvalidJsonException {
        try {
            return objectMapper.readTree(new InputStreamReader(jsonStream, charset));
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public String toJson(Object obj) {
        if (!(obj instanceof JsonNode)) {
            throw new JsonPathException("Not a JSON Node");
        }
        return toString();
    }

    @Override
    public Object createArray() {
        return JsonNodeFactory.instance.arrayNode();
    }

    @Override
    public Object createMap() {
        return JsonNodeFactory.instance.objectNode();
    }

    public Object unwrap(Object o) {

        if (o == null) {
            return null;
        }
        if (!(o instanceof JsonNode)) {
            return o;
        }

        JsonNode e = (JsonNode) o;

        if (e.isValueNode()) {

            if (e.isTextual()) {
                return e.asText();
            } else if (e.isBoolean()) {
                return e.asBoolean();
            } else if (e.isInt()) {
                return e.asInt();
            } else if (e.isLong()) {
                return e.asLong();
            } else if (e.isBigDecimal()) {
                return e.decimalValue();
            } else if (e.isDouble()) {
                return e.doubleValue();
            } else if (e.isFloat()) {
                return e.floatValue();
            } else if (e.isBigDecimal()) {
                return e.decimalValue();
            } else if (e.isNull()) {
                return null;
            }
        }
        return o;
    }


    @Override
    public boolean isArray(Object obj) {
        return (obj instanceof ArrayNode || obj instanceof List);
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
            toJsonArray(array).insert( index, createJsonElement(newValue));
        }
    }

    @Override
    public Object getMapValue(Object obj, String key) {
        ObjectNode jsonObject = toJsonObject(obj);
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
            toJsonObject(obj).put(key.toString(), createJsonElement(value));
        else {
            ArrayNode array = toJsonArray(obj);
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
            ArrayNode array = toJsonArray(obj);
            int index = key instanceof Integer ? (Integer) key : Integer.parseInt(key.toString());
            array.remove(index);
        }
    }

    @Override
    public boolean isMap(Object obj) {
        return (obj instanceof ObjectNode);
    }

    @Override
    public Collection<String> getPropertyKeys(Object obj) {
        List<String> keys = new ArrayList<String>();

        Iterator<String> iter = toJsonObject(obj).fieldNames();
        while (iter.hasNext()){
            keys.add(iter.next());
        }
        return keys;
    }

    @Override
    public int length(Object obj) {
        if (isArray(obj)) {
            return toJsonArray(obj).size();
        } else if (isMap(obj)) {
            return toJsonObject(obj).size();
        } else {
            if (obj instanceof TextNode) {
                TextNode element = (TextNode) obj;
                return element.size();
            }
        }
        throw new JsonPathException("length operation can not applied to " + obj != null ? obj.getClass().getName() : "null");
    }

    @Override
    public Iterable<?> toIterable(Object obj) {
        if (isArray(obj)) {
            ArrayNode arr = toJsonArray(obj);
            List<Object> values = new ArrayList<Object>(arr.size());
            for (Object o : arr) {
                values.add(unwrap(o));
            }
            return values;
        } else {
            List<Object> values = new ArrayList<Object>();
            Iterator<JsonNode> iter = toJsonObject(obj).elements();
            while (iter.hasNext()){
                values.add(unwrap(iter.next()));
            }
            return values;
        }
    }

    private JsonNode createJsonElement(Object o) {
        return objectMapper.valueToTree(o);
    }

    private ArrayNode toJsonArray(Object o) {
        return (ArrayNode) o;
    }

    private ObjectNode toJsonObject(Object o) {
        return (ObjectNode) o;
    }


}
