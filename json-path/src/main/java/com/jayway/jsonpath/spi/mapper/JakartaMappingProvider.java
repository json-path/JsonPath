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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

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

	@SuppressWarnings("unchecked")
	@Override
	public <T> T map(Object source, Class<T> targetType, Configuration configuration) {
		if (source == null || source == JsonValue.NULL) {
			return null;
		}
		if (source == JsonValue.TRUE) {
			if (Boolean.class.equals(targetType)) {
				return (T) Boolean.TRUE;
			} else {
				String cls = targetType.getSimpleName();
				throw new MappingException("JSON boolean (true) cannot be mapped to " + cls);
			}
		}
		if (source == JsonValue.FALSE) {
			if (Boolean.class.equals(targetType)) {
				return (T) Boolean.FALSE;
			} else {
				String cls = targetType.getSimpleName();
				throw new MappingException("JSON boolean (false) cannot be mapped to " + cls);
			}
		}
		if (source instanceof JsonObjectBuilder) {
			source = ((JsonObjectBuilder) source).build();
		} else if (source instanceof JsonArrayBuilder) {
			source = ((JsonArrayBuilder) source).build();
		} else if (source instanceof JsonNumber) {
			if (Number.class.isAssignableFrom(targetType)) {
				JsonNumber jsonNumber = (JsonNumber) source;
				if (jsonNumber.isIntegral()) {
					//return (T) jsonNumber.bigIntegerValueExact();
					return (T) Long.valueOf(jsonNumber.longValueExact());
				} else {
					//return (T) jsonNumber.bigDecimalValue();
					return (T) Double.valueOf(jsonNumber.doubleValue());
				}
			} else {
				String cls = targetType.getSimpleName();
				throw new MappingException("JSON number cannot be mapped to " + cls);
			}
		} else if (source instanceof JsonString) {
			if (String.class.equals(targetType)) {
				return (T) ((JsonString) source).toString();
			} else {
				String cls = targetType.getSimpleName();
				throw new MappingException("JSON string cannot be mapped to " + cls);
			}
		} else if (!(source instanceof JsonStructure)) {
			return (T) source;
		}
		try {
			if (jsonToClassMethod != null) {
				JsonParser jsonParser = new JsonStructureToParserAdapter((JsonStructure) source);
				return (T) jsonToClassMethod.invoke(jsonb, jsonParser, targetType);
			} else {
				// Fallback databinding approach for JSON-B API implementations without
				// explicit support for use of JsonParser in their public API. The approach
				// is essentially first to serialize given value into JSON, and then bind
				// it to data object of given class.
				String json = source.toString();
				return jsonb.fromJson(json, targetType);
			}
		} catch (Exception e){
			throw new MappingException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T map(Object source, final TypeRef<T> targetType, Configuration configuration) {
		if (source == null || source == JsonValue.NULL) {
			return null;
		}
		if (source == JsonValue.TRUE) {
			if (Boolean.class.equals(targetType.getType())) {
				return (T) Boolean.TRUE;
			} else {
				String cls = targetType.getType().toString();
				throw new MappingException("JSON boolean (true) cannot be mapped to " + cls);
			}
		}
		if (source == JsonValue.FALSE) {
			if (Boolean.class.equals(targetType.getType())) {
				return (T) Boolean.FALSE;
			} else {
				String cls = targetType.getType().toString();
				throw new MappingException("JSON boolean (false) cannot be mapped to " + cls);
			}
		}
		if (source instanceof JsonObjectBuilder) {
			source = ((JsonObjectBuilder) source).build();
		} else if (source instanceof JsonArrayBuilder) {
			source = ((JsonArrayBuilder) source).build();
		} else if (source instanceof JsonNumber) {
			JsonNumber jsonNumber = (JsonNumber) source;
			if (jsonNumber.isIntegral()) {
				//return (T) jsonNumber.bigIntegerValueExact();
				return (T) Long.valueOf(jsonNumber.longValueExact());
			} else {
				//return (T) jsonNumber.bigDecimalValue();
				return (T) Double.valueOf(jsonNumber.doubleValue());
			}
		} else if (source instanceof JsonString) {
			if (String.class.equals(targetType.getType())) {
				return (T) ((JsonString) source).toString();
			} else {
				String cls = targetType.getType().toString();
				throw new MappingException("JSON string cannot be mapped to " + cls);
			}
		} else if (!(source instanceof JsonStructure)) {
			return (T) source;
		}
		try {
			if (jsonToTypeMethod != null) {
				JsonParser jsonParser = new JsonStructureToParserAdapter((JsonStructure) source);
				return (T) jsonToTypeMethod.invoke(jsonb, jsonParser, targetType.getType());
			} else {
				// Fallback databinding approach for JSON-B API implementations without
				// explicit support for use of JsonParser in their public API. The approach
				// is essentially first to serialize given value into JSON, and then bind
				// the JSON string to data object of given type.
				String json = source.toString();
				return jsonb.fromJson(json, targetType.getType());
			}
		} catch (Exception e){
			throw new MappingException(e);
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
