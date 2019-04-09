package com.jayway.jsonpath.impl.mapper;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

public class GsonDateAdapter implements JsonDeserializer<Date> {

	@Override
	public Date deserialize(JsonElement element, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		String date = element.getAsString();		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");		
		try {
			return formatter.parse(date);
		} catch (ParseException e) {
			if(element.isJsonPrimitive()) {
				long longDate = 0;
				JsonPrimitive primitive =  element.getAsJsonPrimitive();
				if(primitive.isNumber()) {
					longDate = primitive.getAsNumber().longValue();
				} else {
					try {
						longDate = Long.parseLong(primitive.getAsString());
					} catch(NumberFormatException ignore) {}
				}
				if(longDate > 0) {
					return new Date(longDate);
				}
			}			
			throw new JsonParseException(e);
			
		}
	}
}