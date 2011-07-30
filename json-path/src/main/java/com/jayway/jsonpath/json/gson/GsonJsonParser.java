package com.jayway.jsonpath.json.gson;

import java.io.IOException;

import net.minidev.json.parser.JSONParser;

import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.ParseException;

public class GsonJsonParser implements com.jayway.jsonpath.json.JsonParser {

    private static com.google.gson.JsonParser JSON_PARSER = new com.google.gson.JsonParser();
    
	public JsonElement parse(String json) throws ParseException,IOException {
		try {
			return GsonUtil.convertUp(JSON_PARSER.parse(json));
		} catch (JsonException e) {
			throw new ParseException(e);
		}
	}

}
