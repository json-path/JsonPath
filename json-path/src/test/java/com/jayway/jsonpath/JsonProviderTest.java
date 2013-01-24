package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.impl.JacksonProvider;
import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

import static com.jayway.jsonpath.JsonModel.model;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 10:40 PM
 */
public class JsonProviderTest {
    private static final Logger logger = LoggerFactory.getLogger(JsonPathTest.class);

    public final static String ARRAY = "[{\"value\": 1},{\"value\": 2}, {\"value\": 3},{\"value\": 4}]";

    public final static String DOCUMENT =
            "{ \"store\": {\n" +
                    "    \"book\": [ \n" +
                    "      { \"category\": \"reference\",\n" +
                    "        \"author\": \"Nigel Rees\",\n" +
                    "        \"title\": \"Sayings of the Century\",\n" +
                    "        \"price\": 8.95\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Evelyn Waugh\",\n" +
                    "        \"title\": \"Sword of Honour\",\n" +
                    "        \"price\": 12.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"Herman Melville\",\n" +
                    "        \"title\": \"Moby Dick\",\n" +
                    "        \"isbn\": \"0-553-21311-3\",\n" +
                    "        \"price\": 8.99\n" +
                    "      },\n" +
                    "      { \"category\": \"fiction\",\n" +
                    "        \"author\": \"J. R. R. Tolkien\",\n" +
                    "        \"title\": \"The Lord of the Rings\",\n" +
                    "        \"isbn\": \"0-395-19395-8\",\n" +
                    "        \"price\": 22.99\n" +
                    "      }\n" +
                    "    ],\n" +
                    "    \"bicycle\": {\n" +
                    "      \"color\": \"red\",\n" +
                    "      \"price\": 19.95,\n" +
                    "      \"foo:bar\": \"fooBar\",\n" +
                    "      \"dot.notation\": \"new\"\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";


    @Test
    public void clone_test() throws Exception {

        Serializable jsonObject = (Serializable) model(DOCUMENT).getJsonObject();

        Object clone = SerializationUtils.clone(jsonObject);

        logger.debug(model(clone).toJson());

    }


    @Test
    public void parse_document() throws Exception {

        JacksonProvider provider = new JacksonProvider();

        Object o = provider.parse(DOCUMENT);

        logger.debug("{}", o);

    }

    @Test
    public void parse_array() throws Exception {
        JacksonProvider provider = new JacksonProvider();

        Object o = provider.parse(ARRAY);

        logger.debug("{}", o);
    }
}
