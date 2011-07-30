package com.jayway.jsonpath.json.minidev;

import java.io.IOException;

import net.minidev.json.parser.JSONParser;

import com.jayway.jsonpath.json.JsonElement;
import com.jayway.jsonpath.json.JsonException;
import com.jayway.jsonpath.json.ParseException;

public class MiniJsonParser implements com.jayway.jsonpath.json.JsonParser {
    
    private static JSONParser  JSON_PARSER = new JSONParser(JSONParser.MODE_PERMISSIF);
    
	public JsonElement parse(String json) throws ParseException,IOException {
		try {
			return MiniUtil.convertUp(JSON_PARSER.parse(json));
			
		}  catch (JsonException e) {
			throw new ParseException(e);
		}
		catch (net.minidev.json.parser.ParseException e) {
			throw new ParseException(e);
		}
	}

}
