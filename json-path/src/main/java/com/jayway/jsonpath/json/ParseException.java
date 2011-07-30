package com.jayway.jsonpath.json;



public class ParseException extends Exception {

	public ParseException() {
		super();
	}
	public ParseException(String s) {
		super(s);
	}
	
	public ParseException(Throwable e) {
		super(e);
	}
	public ParseException(String s,Throwable e) {
		super(s,e);
	}

}
