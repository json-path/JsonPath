package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JSONAsserterImpl;
import com.jayway.jsonassert.impl.JSONReaderImpl;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

/**
 * User: kalle stenflo
 * Date: 1/24/11
 * Time: 9:31 PM
 */
public class JSONAssert {

    /**
     * Creates a JSONReader
     *
     * @param jsonDoc the json document to read
     * @return a new reader
     * @throws ParseException
     */
    public static JSONReader parse(String jsonDoc) throws ParseException {
        return JSONReaderImpl.parse(jsonDoc);
    }

    /**
     * Creates a JSONReader
     *
     * @param reader he json document to read
     * @return a new reader
     * @throws ParseException document could not pe parsed
     * @throws IOException
     */
    public static JSONReader parse(Reader reader) throws ParseException, IOException {
        return JSONReaderImpl.parse(reader);
    }

    /**
     * Creates a JSONAsserter
     *
     * @param json the JSON document to create a JSONAsserter for
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JSONAsserter with(String json) throws ParseException {
        return new JSONAsserterImpl(JSONReaderImpl.parse(json));
    }

    /**
     * Creates a JSONAsserter
     *
     * @param reader the reader of the json document
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JSONAsserter with(Reader reader) throws ParseException, IOException {
        return new JSONAsserterImpl(JSONReaderImpl.parse(reader));
    }

}
