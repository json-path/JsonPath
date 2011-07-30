package com.jayway.jsonpath.filter;

public enum JsonType{
	
	JsonNull("null"),
	JsonArray("collection"),
	JsonObject("object"),
	JsonPrimitive("value");

	private String text;

	  JsonType(String text) {
	    this.text = text;
	  }

	  public String getText() {
	    return this.text;
	  }

	  public static JsonType fromString(String text) {
	    if (text != null) {
	      for (JsonType b : JsonType.values()) {
	        if (text.equalsIgnoreCase(b.text)) {
	          return b;
	        }
	      }
	    }
	    return null;
	    
	  }
}