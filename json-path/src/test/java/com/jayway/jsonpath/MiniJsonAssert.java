package com.jayway.jsonpath;

import org.junit.Before;

import com.jayway.jsonpath.json.JsonFactory;

public class MiniJsonAssert extends JsonPathTest {
	
	public void init_factory(){
		JsonFactory.setInstance(new com.jayway.jsonpath.json.minidev.MiniJsonFactory());
		factory = JsonFactory.getInstance();
	}
}
