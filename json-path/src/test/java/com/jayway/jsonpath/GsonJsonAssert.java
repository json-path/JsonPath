package com.jayway.jsonpath;

import org.junit.Before;

import com.jayway.jsonpath.json.JsonFactory;

public class GsonJsonAssert extends JsonPathTest {
	
	public void init_factory(){
		JsonFactory.setInstance(new com.jayway.jsonpath.json.gson.GsonJsonFactory());
		factory = JsonFactory.getInstance();	
	}
}
