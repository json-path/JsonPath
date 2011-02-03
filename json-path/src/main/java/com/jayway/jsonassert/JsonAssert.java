package com.jayway.jsonassert;

import com.jayway.jsonassert.impl.JsonAsserterImpl;
import com.jayway.jsonassert.impl.JsonPathImpl;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

/**
 * User: kalle stenflo
 * Date: 1/24/11
 * Time: 9:31 PM
 */
public class JsonAssert {

    /**
     * Creates a JSONReader
     *
     * @param jsonDoc the json document to read
     * @return a new reader
     * @throws ParseException
     */
    public static JsonPath parse(String jsonDoc) throws ParseException {
        return JsonPathImpl.parse(jsonDoc);
    }

    /**
     * Creates a JSONReader
     *
     * @param reader he json document to read
     * @return a new reader
     * @throws ParseException document could not pe parsed
     * @throws IOException
     */
    public static JsonPath parse(Reader reader) throws ParseException, IOException {
        return parse(reader, false);
    }

    /**
     * Creates a JSONReader
     *
     * @param reader he json document to read
     * @return a new reader
     * @throws ParseException document could not pe parsed
     * @throws IOException
     */
    public static JsonPath parse(Reader reader, boolean closeReader) throws ParseException, IOException {
        JsonPath jsonReader = null;
        try {
            jsonReader = JsonPathImpl.parse(reader);
        } finally {
            if(closeReader){
                try {
                    reader.close();
                } catch (IOException ignore) {}
            }
        }
        return jsonReader;
    }

    /**
     * Creates a JSONAsserter
     *
     * @param json the JSON document to create a JSONAsserter for
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JsonAsserter with(String json) throws ParseException {
        return new JsonAsserterImpl(JsonPathImpl.parse(json));
    }

    /**
     * Creates a JSONAsserter
     *
     * @param reader the reader of the json document
     * @return a JSON asserter initialized with the provided document
     * @throws ParseException when the given JSON could not be parsed
     */
    public static JsonAsserter with(Reader reader) throws ParseException, IOException {
        return new JsonAsserterImpl(JsonPathImpl.parse(reader));
    }

}
