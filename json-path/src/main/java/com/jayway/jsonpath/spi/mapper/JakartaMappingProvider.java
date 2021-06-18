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
package com.jayway.jsonpath.spi.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.TypeRef;

import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonException;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonString;
import jakarta.json.JsonStructure;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonLocation;
import jakarta.json.stream.JsonParser;

public class JakartaMappingProvider implements MappingProvider {

    private final Jsonb jsonb;
    private Method jsonToClassMethod, jsonToTypeMethod;

    public JakartaMappingProvider() {
        this.jsonb = JsonbBuilder.create();
        this.jsonToClassMethod = findMethod(jsonb.getClass(), "fromJson", JsonParser.class, Class.class);
        this.jsonToTypeMethod = findMethod(jsonb.getClass(), "fromJson", JsonParser.class, Type.class);
    }

    public JakartaMappingProvider(JsonbConfig jsonbConfiguration) {
        this.jsonb = JsonbBuilder.create(jsonbConfiguration);
        this.jsonToClassMethod = findMethod(jsonb.getClass(), "fromJson", JsonParser.class, Class.class);
        this.jsonToTypeMethod = findMethod(jsonb.getClass(), "fromJson", JsonParser.class, Type.class);
    }

    /**
     * Maps supplied JSON source {@code Object} to a given target class or collection.
     * This implementation ignores the JsonPath's {@link Configuration} argument.
     */
    @Override
    public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
        @SuppressWarnings("unchecked")
        T result = (T) mapImpl(source, targetType);
        return result;
    }

    /**
     * Maps supplied JSON source {@code Object} to a given target type or collection.
     * This implementation ignores the JsonPath's {@link Configuration} argument.
     * <p>
     * Method <em>may</em> produce a {@code ClassCastException} on an attempt to cast
     * the result of JSON mapping operation to a requested target type, especially if
     * a parameterized generic type is used.
     */
    @Override
    public <T> T map(Object source, final TypeRef<T> targetType, Configuration configuration) {
        @SuppressWarnings("unchecked")
        T result = (T) mapImpl(source, targetType.getType());
        return result;
    }

    private Object mapImpl(Object source, final Type targetType) {
        if (source == null || source == JsonValue.NULL) {
            return null;
        }
        if (source == JsonValue.TRUE) {
            if (Boolean.class.equals(targetType)) {
                return Boolean.TRUE;
            } else {
                String className = targetType.toString();
                throw new MappingException("JSON boolean (true) cannot be mapped to " + className);
            }
        }
        if (source == JsonValue.FALSE) {
            if (Boolean.class.equals(targetType)) {
                return Boolean.FALSE;
            } else {
                String className = targetType.toString();
                throw new MappingException("JSON boolean (false) cannot be mapped to " + className);
            }
        } else if (source instanceof JsonString) {
            if (String.class.equals(targetType)) {
                return ((JsonString) source).getChars();
            } else {
                String className = targetType.toString();
                throw new MappingException("JSON string cannot be mapped to " + className);
            }
        } else if (source instanceof JsonNumber) {
            JsonNumber jsonNumber = (JsonNumber) source;
            if (jsonNumber.isIntegral()) {
                return mapIntegralJsonNumber(jsonNumber, getRawClass(targetType));
            } else {
                return mapDecimalJsonNumber(jsonNumber, getRawClass(targetType));
            }
        }
        if (source instanceof JsonArrayBuilder) {
            source = ((JsonArrayBuilder) source).build();
        } else if (source instanceof JsonObjectBuilder) {
            source = ((JsonObjectBuilder) source).build();
        }
        if (source instanceof Collection) {
            // this covers both List<JsonValue> and JsonArray from JSON-P spec
            Class<?> rawTargetType = getRawClass(targetType);
            Type targetTypeArg = getFirstTypeArgument(targetType);
            Collection<Object> result = newCollectionOfType(rawTargetType);
            for (Object srcValue : (Collection<?>) source) {
                if (srcValue instanceof JsonObject) {
                    if (targetTypeArg != null) {
                        result.add(mapImpl(srcValue, targetTypeArg));
                    } else {
                        result.add(srcValue);
                    }
                } else {
                    result.add(unwrapJsonValue(srcValue));
                }
            }
            return result;
        } else if (source instanceof JsonObject) {
            if (targetType instanceof Class) {
                if (jsonToClassMethod != null) {
                    try {
                        JsonParser jsonParser = new JsonStructureToParserAdapter((JsonStructure) source);
                        return jsonToClassMethod.invoke(jsonb, jsonParser, (Class<?>) targetType);
                    } catch (Exception e){
                        throw new MappingException(e);
                    }
                } else {
                    try {
                        // Fallback databinding approach for JSON-B API implementations without
                        // explicit support for use of JsonParser in their public API. The approach
                        // is essentially first to serialize given value into JSON, and then bind
                        // it to data object of given class.
                        String json = source.toString();
                        return jsonb.fromJson(json, (Class<?>) targetType);
                    } catch (JsonbException e){
                        throw new MappingException(e);
                    }
                }
            } else if (targetType instanceof ParameterizedType) {
                if (jsonToTypeMethod != null) {
                    try {
                        JsonParser jsonParser = new JsonStructureToParserAdapter((JsonStructure) source);
                        return jsonToTypeMethod.invoke(jsonb, jsonParser, (Type) targetType);
                    } catch (Exception e){
                        throw new MappingException(e);
                    }
                } else {
                    try {
                        // Fallback databinding approach for JSON-B API implementations without
                        // explicit support for use of JsonParser in their public API. The approach
                        // is essentially first to serialize given value into JSON, and then bind
                        // the JSON string to data object of given type.
                        String json = source.toString();
                        return jsonb.fromJson(json, (Type) targetType);
                    } catch (JsonbException e){
                        throw new MappingException(e);
                    }
                }
            } else {
                throw new MappingException("JSON object cannot be databind to " + targetType);
            }
        } else {
            return source;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T mapIntegralJsonNumber(JsonNumber jsonNumber, Class<?> targetType) {
        if (targetType.isPrimitive()) {
            if (int.class.equals(targetType)) {
                return (T) Integer.valueOf(jsonNumber.intValueExact());
            } else if (long.class.equals(targetType)) {
                return (T) Long.valueOf(jsonNumber.longValueExact());
            }
        } else if (Integer.class.equals(targetType)) {
            return (T) Integer.valueOf(jsonNumber.intValueExact());
        } else if (Long.class.equals(targetType)) {
            return (T) Long.valueOf(jsonNumber.longValueExact());
        } else if (BigInteger.class.equals(targetType)) {
            return (T) jsonNumber.bigIntegerValueExact();
        } else if (BigDecimal.class.equals(targetType)) {
            return (T) jsonNumber.bigDecimalValue();
        }

        String className = targetType.getSimpleName();
        throw new MappingException("JSON integral number cannot be mapped to " + className);
    }

    @SuppressWarnings("unchecked")
    private <T> T mapDecimalJsonNumber(JsonNumber jsonNumber, Class<?> targetType) {
        if (targetType.isPrimitive()) {
            if (float.class.equals(targetType)) {
                return (T) new Float(jsonNumber.doubleValue());
            } else if (double.class.equals(targetType)) {
                return (T) Double.valueOf(jsonNumber.doubleValue());
            }
        } else if (Float.class.equals(targetType)) {
            return (T) new Float(jsonNumber.doubleValue());
        } else if (Double.class.equals(targetType)) {
            return (T) Double.valueOf(jsonNumber.doubleValue());
        } else if (BigDecimal.class.equals(targetType)) {
            return (T) jsonNumber.bigDecimalValue();
        }

        String className = targetType.getSimpleName();
        throw new MappingException("JSON decimal number cannot be mapped to " + className);
    }

    private Object unwrapJsonValue(Object jsonValue) {
        if (jsonValue == null) {
            return null;
        }
        if (!(jsonValue instanceof JsonValue)) {
            return jsonValue;
        }
        switch (((JsonValue) jsonValue).getValueType()) {
        case ARRAY:
        	// TODO do we unwrap JsonObjectArray proxies?
            //return ((JsonArray) jsonValue).getValuesAs(JsonValue.class);
            return ((JsonArray) jsonValue).getValuesAs((JsonValue v) -> unwrapJsonValue(v));
        case OBJECT:
            throw new IllegalArgumentException("Use map() method to databind a JsonObject");
        case STRING:
            return ((JsonString) jsonValue).getString();
        case NUMBER:
            if (((JsonNumber) jsonValue).isIntegral()) {
                //return ((JsonNumber) jsonValue).bigIntegerValueExact();
                try {
                    return ((JsonNumber) jsonValue).intValueExact();
                } catch (ArithmeticException e) {
                    return ((JsonNumber) jsonValue).longValueExact();
                }
            } else {
                //return ((JsonNumber) jsonValue).bigDecimalValue();
                return ((JsonNumber) jsonValue).doubleValue();
            }
        case TRUE:
            return Boolean.TRUE;
        case FALSE:
            return Boolean.FALSE;
        case NULL:
            return null;
        default:
            return jsonValue;
        }
    }

    /**
     * Creates new instance of {@code Collection} type specified by the
     * argument. If the argument refers to an interface, then a matching
     * Java standard implementation is returned; if it is a concrete class,
     * then method attempts to instantiate an object of that class given
     * there is a public no-arg constructor available.
     *
     * @param collectionType collection type; may be an interface or a class
     * @return instance of collection type identified by the argument
     * @throws MappingException on a type that cannot be safely instantiated
     */
    private Collection<Object> newCollectionOfType(Class<?> collectionType) throws MappingException {
        if (Collection.class.isAssignableFrom(collectionType)) {
            if (!collectionType.isInterface()) {
                @SuppressWarnings("unchecked")
                Collection<Object> coll = (Collection<Object>) newNoArgInstance(collectionType);
                return coll;
            } else if (List.class.isAssignableFrom(collectionType)) {
                return new java.util.LinkedList<Object>();
            } else if (Set.class.isAssignableFrom(collectionType)) {
                return new java.util.LinkedHashSet<Object>();
            } else if (Queue.class.isAssignableFrom(collectionType)) {
                return new java.util.LinkedList<Object>();
            }
        }
        String className = collectionType.getSimpleName();
        throw new MappingException("JSON array cannot be mapped to " + className);
    }

    /**
     * Lists all publicly accessible constructors for the {@code Class}
     * identified by the argument, including any constructors inherited
     * from superclasses, and uses a no-args constructor, if available,
     * to create a new instance of the class. If argument is interface, 
     * this method returns {@code null}.
     * 
     * @param targetType class type to create instance of
     * @return an instance of the class represented by the argument
     * @throws MappingException if no-arg public constructor is not there 
     */
    private Object newNoArgInstance(Class<?> targetType) throws MappingException {
        if (targetType.isInterface()) {
            return null;
        } else {
            for (Constructor<?> ctr : targetType.getConstructors()) {
                if (ctr.getParameterCount() == 0) {
                    try {
                        return ctr.newInstance();
                    } catch (ReflectiveOperationException e) {
                        throw new MappingException(e);
                    } catch (IllegalArgumentException e) {
                        // never happens
                    }
                }
            }
            String className = targetType.getSimpleName();
            throw new MappingException("Unable to find no-arg ctr for " + className);
        }
    }

    /**
     * Locates optional API method on the supplied JSON-B API implementation class with
     * the supplied name and parameter types. Searches the superclasses up to
     * {@code Object}, but ignores interfaces and default interface methods. Returns
     * {@code null} if no {@code Method} can be found. 
     *
     * @param clazz the implementation class to reflect upon
     * @param name the name of the method
     * @param paramTypes the parameter types of the method
     * @return the {@code Method} reference, or {@code null} if none found
     */
    private Method findMethod(Class<?> clazz, String name, Class<?>... paramTypes) {
        while (clazz != null && !clazz.isInterface()) {
            for (Method method : clazz.getDeclaredMethods()) {
                final int mods = method.getModifiers();
                if (Modifier.isPublic(mods) && !Modifier.isAbstract(mods) &&
                        name.equals(method.getName()) &&
                        Arrays.equals(paramTypes, method.getParameterTypes())) {
                    return method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private Class<?> getRawClass(Type targetType) {
        if (targetType instanceof Class) {
            return (Class<?>) targetType;
        } else if (targetType instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) targetType).getRawType();
        } else if (targetType instanceof GenericArrayType) {
            String typeName = targetType.getTypeName();
            throw new MappingException("Cannot map JSON element to " + typeName);
        } else {
            String typeName = targetType.getTypeName();
            throw new IllegalArgumentException("TypeRef not supported: " + typeName);
        }
    }

    private Type getFirstTypeArgument(Type targetType) {
        if (targetType instanceof ParameterizedType) {
            Type[] args = ((ParameterizedType) targetType).getActualTypeArguments();
            if (args != null && args.length > 0) {
                if (args[0] instanceof Class) {
                    return (Class<?>) args[0];
                } else if (args[0] instanceof ParameterizedType) {
                    return (ParameterizedType) args[0];
                }
            }
        }
        return null;
    }

    /**
     * Runtime adapter for {@link JsonParser} to pull the JSON objects and values from
     * a {@link JsonStructure} content tree instead of plain JSON string.
     * <p>
     * JSON-B API 1.0 final specification does not include any public methods to read JSON
     * content from pre-parsed {@link JsonStructure} tree, so this parser is used by the
     * Jakarta EE mapping provider above to feed in JSON content to JSON-B implementation.
     */
    private static class JsonStructureToParserAdapter implements JsonParser {

        private JsonStructureScope scope;
        private Event state;
        private final Deque<JsonStructureScope> ancestry = new ArrayDeque<>();

        JsonStructureToParserAdapter(JsonStructure jsonStruct) {
            scope = createScope(jsonStruct);
        }

        @Override
        public boolean hasNext() {
            return !((state == Event.END_ARRAY || state == Event.END_OBJECT) && ancestry.isEmpty());
        }

        @Override
        public Event next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (state == null) {
                state = scope instanceof JsonArrayScope ? Event.START_ARRAY : Event.START_OBJECT;
            } else {
                if (state == Event.END_ARRAY || state == Event.END_OBJECT) {
                    scope = ancestry.pop();
                }
                if (scope instanceof JsonArrayScope) { // array scope
                    if (scope.hasNext()) {
                        scope.next();
                        state = getState(scope.getValue());
                        if (state == Event.START_ARRAY || state == Event.START_OBJECT) {
                            ancestry.push(scope);
                            scope = createScope(scope.getValue());
                        }
                    } else {
                        state = Event.END_ARRAY;
                    }
                } else { // object scope
                    if (state == Event.KEY_NAME) {
                        state = getState(scope.getValue());
                        if (state == Event.START_ARRAY || state == Event.START_OBJECT) {
                            ancestry.push(scope);
                            scope = createScope(scope.getValue());
                        }
                    } else {
                        if (scope.hasNext()) {
                            scope.next();
                            state = Event.KEY_NAME;
                        } else {
                            state = Event.END_OBJECT;
                        }
                    }
                }
            }
            return state;
        }

        @Override
        public String getString() {
            switch (state) {
            case KEY_NAME:
                return ((JsonObjectScope) scope).getKey();
            case VALUE_STRING:
                return ((JsonString) scope.getValue()).getString();
            case VALUE_NUMBER:
                return ((JsonNumber) scope.getValue()).toString();
            default:
                throw new IllegalStateException("Parser is not in KEY_NAME, VALUE_STRING, or VALUE_NUMBER state");
            }
        }

        @Override
        public boolean isIntegralNumber() {
            if (state == Event.VALUE_NUMBER) {
                return ((JsonNumber) scope.getValue()).isIntegral();
            }
            throw new IllegalStateException("Target json value must a number, not " + state);
        }

        @Override
        public int getInt() {
            if (state == Event.VALUE_NUMBER) {
                return ((JsonNumber) scope.getValue()).intValue();
            }
            throw new IllegalStateException("Target json value must a number, not " + state);
        }

        @Override
        public long getLong() {
            if (state == Event.VALUE_NUMBER) {
                return ((JsonNumber) scope.getValue()).longValue();
            }
            throw new IllegalStateException("Target json value must a number, not " + state);
        }

        @Override
        public BigDecimal getBigDecimal() {
            if (state == Event.VALUE_NUMBER) {
                return ((JsonNumber) scope.getValue()).bigDecimalValue();
            }
            throw new IllegalStateException("Target json value must a number, not " + state);
        }

        @Override
        public JsonLocation getLocation() {
            throw new UnsupportedOperationException("JSON-P adapter does not support getLocation()");
        }

        @Override
        public void skipArray() {
            if (scope instanceof JsonArrayScope) {
                while (scope.hasNext()) {
                    scope.next();
                }
                state = Event.END_ARRAY;
            }
        }

        @Override
        public void skipObject() {
            if (scope instanceof JsonObjectScope) {
                while (scope.hasNext()) {
                    scope.next();
                }
                state = Event.END_OBJECT;
            }
        }

        @Override
        public void close() {
            // JSON objects are read-only
        }

        private JsonStructureScope createScope(JsonValue value) {
            if (value instanceof JsonArray) {
                return new JsonArrayScope((JsonArray) value);
            } else if (value instanceof JsonObject) {
                return new JsonObjectScope((JsonObject) value);
            }
            throw new JsonException("Cannot create JSON iterator for " + value);
        }

        private Event getState(JsonValue value) {
            switch (value.getValueType()) {
            case ARRAY:
                return Event.START_ARRAY;
            case OBJECT:
                return Event.START_OBJECT;
            case STRING:
                return Event.VALUE_STRING;
            case NUMBER:
                return Event.VALUE_NUMBER;
            case TRUE:
                return Event.VALUE_TRUE;
            case FALSE:
                return Event.VALUE_FALSE;
            case NULL:
                return Event.VALUE_NULL;
            default:
                throw new JsonException("Unknown value type " + value.getValueType());
            }
        }
    }

    private static abstract class JsonStructureScope implements Iterator<JsonValue> {
        /**
         * Returns current {@link JsonValue}, that the parser is pointing on. Before
         * the {@link #next()} method has been called, this returns {@code null}.
         *
         * @return JsonValue value object.
         */
        abstract JsonValue getValue();
    }

    private static class JsonArrayScope extends JsonStructureScope {
        private final Iterator<JsonValue> it;
        private JsonValue value;

        JsonArrayScope(JsonArray array) {
            this.it = array.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public JsonValue next() {
            value = it.next();
            return value;
        }

        @Override
        JsonValue getValue() {
            return value;
        }
    }

    private static class JsonObjectScope extends JsonStructureScope {
        private final Iterator<Map.Entry<String, JsonValue>> it;
        private JsonValue value;
        private String key;

        JsonObjectScope(JsonObject object) {
            this.it = object.entrySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public JsonValue next() {
            Map.Entry<String, JsonValue> next = it.next();
            this.key = next.getKey();
            this.value = next.getValue();
            return value;
        }

        @Override
        JsonValue getValue() {
            return value;
        }

        String getKey() {
            return key;
        }
    }
}
