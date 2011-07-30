package com.jayway.jsonassert;

import org.junit.Before;

import com.jayway.jsonpath.json.JsonFactory;

public class MiniJsonAssert extends JsonAssertTest {
	@Before
	public void init_factory(){
		factory.setInstance(com.jayway.jsonpath.json.minidev.MiniJsonFactory.getInstance());
		factory = JsonFactory.getInstance();
	}
}
