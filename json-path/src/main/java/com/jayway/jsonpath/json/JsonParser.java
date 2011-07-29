package com.jayway.jsonpath.json;

import java.io.IOException;

public interface JsonParser  {

	public JsonElement parse(String json) throws ParseException,IOException;

}
