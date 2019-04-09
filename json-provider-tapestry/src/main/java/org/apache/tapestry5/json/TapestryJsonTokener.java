package org.apache.tapestry5.json;

import java.io.IOException;
import java.io.Reader;


public class TapestryJsonTokener extends JSONTokener {

	public static void testValidity(Object o) {
		JSONObject.testValidity(o);
	}
	
	public TapestryJsonTokener(Reader reader) throws IOException {
		super(reader);
	}

	public TapestryJsonTokener(String s) {
		super(s);
	}
	
    public Object nextValue(){
      return super.nextValue(null);
    }

}
