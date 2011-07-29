package com.jayway.jsonassert;


import com.jayway.jsonassert.impl.JsonAsserterImpl;
import com.jayway.jsonassert.impl.matcher.*;
import com.jayway.jsonpath.json.JsonFactory;

import net.minidev.json.parser.JSONParser;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsNull;

import java.io.*;
import java.text.ParseException;
import java.util.Collection;
import java.util.Map;

/**
 * User: kalle stenflo
 * Date: 1/24/11
 * Time: 9:31 PM
 */
public class JsonAssert {

    private static JsonFactory JSON_PARSER = JsonFactory.getInstance();

    public final static int STRICT_MODE = 0;
    public final static int SLACK_MODE = -1;

    private static int mode = SLACK_MODE;

    public static void setMode(int mode) {
        if (mode != JsonAssert.mode) {
            JsonAssert.mode = mode;

        }
    }

    public static int getMode() {
        return mode;
    }

    /**
     * Creates a JSONAsserter
     *
     * @param json the JSON document to create a JSONAsserter for
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     * @throws com.jayway.jsonpath.json.ParseException 
     * @throws com.jayway.jsonpath.json.ParseException 
     */
    public static JsonAsserter with(String json) throws  com.jayway.jsonpath.json.ParseException {
        try {

        	return new JsonAsserterImpl(JSON_PARSER.parse(json));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a JSONAsserter
     *
     * @param reader the reader of the json document
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     * @throws com.jayway.jsonpath.json.ParseException 
     */
    public static JsonAsserter with(Reader reader) throws ParseException, IOException, com.jayway.jsonpath.json.ParseException {
      
            return new JsonAsserterImpl(JSON_PARSER.parse(convertReaderToString(reader)));
      
    }

    /**
     * Creates a JSONAsserter
     *
     * @param is the input stream
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     * @throws com.jayway.jsonpath.json.ParseException 
     */
    public static JsonAsserter with(InputStream is) throws ParseException, IOException, com.jayway.jsonpath.json.ParseException {
        Reader reader = new InputStreamReader(is);
        return with(reader);
    }

    //Matchers

    public static CollectionMatcher collectionWithSize(Matcher<? super Integer> sizeMatcher) {
        return new IsCollectionWithSize(sizeMatcher);
    }

    public static Matcher<Map<String, ?>> mapContainingKey(Matcher<String> keyMatcher) {
        return new IsMapContainingKey(keyMatcher);
    }

    public static <V> Matcher<? super Map<?, V>> mapContainingValue(Matcher<? super V> valueMatcher) {
        return new IsMapContainingValue<V>(valueMatcher);
    }

    public static Matcher<Collection<Object>> emptyCollection() {
        return new IsEmptyCollection<Object>();
    }
    public static Matcher<Object> isnull() {
        return new IsNull<Object>();
    }

    private static String convertReaderToString(Reader reader)
            throws IOException {

        if (reader != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                reader.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }


}
