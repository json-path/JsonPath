package com.jayway.jsonassert;

import org.junit.Before;

import com.jayway.jsonpath.json.JsonFactory;

public class GsonJsonAssert extends JsonAssertTest {
	
	public void init_factory(){
		JsonFactory.setInstance(com.jayway.jsonpath.json.gson.GsonJsonFactory.getInstance());
		factory = JsonFactory.getInstance();
	}
}
