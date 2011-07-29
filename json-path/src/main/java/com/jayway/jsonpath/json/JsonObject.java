package com.jayway.jsonpath.json;

import java.util.List;
import java.util.Map;

public abstract class JsonObject extends JsonElement implements Map<String,JsonElement> {

	public abstract	List<JsonElement> getProperties() throws JsonException;

	public abstract boolean hasProperty(String pathFragment)throws JsonException;


	public abstract JsonElement getProperty(String pathFragment) throws JsonException;

	public abstract void setProperty(String name, JsonElement element)		throws JsonException;



}
