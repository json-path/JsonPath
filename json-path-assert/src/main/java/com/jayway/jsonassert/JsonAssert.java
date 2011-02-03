package com.jayway.jsonassert;


import com.jayway.jsonassert.impl.JsonAsserterImpl;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;

/**
 * User: kalle stenflo
 * Date: 1/24/11
 * Time: 9:31 PM
 */
public class JsonAssert {

    private static final JSONParser JSON_PARSER = new JSONParser();

    /**
     * Creates a JSONAsserter
     *
     * @param json the JSON document to create a JSONAsserter for
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JsonAsserter with(String json) throws ParseException {
        try {
            return new JsonAsserterImpl(JSON_PARSER.parse(json));
        } catch (org.json.simple.parser.ParseException e) {
            throw new ParseException(json, e.getPosition());
        }
    }

    /**
     * Creates a JSONAsserter
     *
     * @param reader the reader of the json document
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JsonAsserter with(Reader reader) throws ParseException, IOException {
        try {
            return new JsonAsserterImpl(JSON_PARSER.parse(reader));
        } catch (org.json.simple.parser.ParseException e) {
            throw new ParseException(e.toString(), e.getPosition());
        } finally {
            reader.close();
        }
    }

    /**
     * Creates a JSONAsserter
     *
     * @param is the input stream
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JsonAsserter with(InputStream is) throws ParseException, IOException {
        Reader reader = new InputStreamReader(is);
        return with(reader);
    }

}
