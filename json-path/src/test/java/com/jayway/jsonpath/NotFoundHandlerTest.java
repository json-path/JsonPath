package com.jayway.jsonpath;

import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import net.minidev.json.writer.JsonReader;
import org.junit.Test;

import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class NotFoundHandlerTest {

    @Test
    public void testMixNotFoundHandler() {

        Runnable r = () -> {
            for (int i = 0; i < 10; i++) {

                String exp = "$.sum($.transitModeDistance.walk, $.transitModeDistance.bicycle)";

                String mixEntry = "{\n" +
                        "    \"transitModeDistance\": {\n" +
                        "        \"car\": 47517.0,\n" +
                        "        \"stay\": 0.0,\n" +
                        "        \"walk\": 4.0\n" +
                        "    }\n" +
                        "}";

                Configuration conf1 = Configuration.builder()
                        .mappingProvider(new JsonSmartMappingProvider(new JsonReader()))
                        .jsonProvider(new GsonJsonProvider())
                        .notFoundHandler(notfound -> 0)
                        .build();
                Double result1 = (Double) JsonPath.using(conf1).parse(mixEntry).read(exp);
                assertEquals(new Double(4.0), result1);

            }
        };

        Executors.newCachedThreadPool().submit(r);
        Executors.newCachedThreadPool().submit(r);

        for (int i = 0; i < 10; i++) {

            String exp = "$.sum($.transitModeDistance.walk, $.transitModeDistance.bicycle)";

            String mixEntry = "{\n" +
                    "    \"transitModeDistance\": {\n" +
                    "        \"car\": 47517.0,\n" +
                    "        \"stay\": 0.0,\n" +
                    "        \"walk\": 4.0\n" +
                    "    }\n" +
                    "}";

            Configuration conf1 = Configuration.builder()
                    .mappingProvider(new JsonSmartMappingProvider(new JsonReader()))
                    .jsonProvider(new GsonJsonProvider())
                    .notFoundHandler(notfound -> 0)
                    .build();
            Double result1 = (Double) JsonPath.using(conf1).parse(mixEntry).read(exp);
            assertEquals(new Double(4.0), result1);

        }

    }

    @Test
    public void testEmptySumNotFoundHandler() {

        String exp = "$.sum($.transitModeDistance.walk, $.transitModeDistance.bicycle)  ";

        String emptyEntry = "{\n" +
                "    \"transitModeDistance\": {\n" +
                "        \"car\": 47517.0,\n" +
                "        \"stay\": 0.0\n" +
                "    }\n" +
                "}";

        Configuration conf2 = Configuration.defaultConfiguration().setNotFoundHandler(found -> 1);
        Double result2 = (Double) JsonPath.using(conf2).parse(emptyEntry).read(exp);
        assertEquals(new Double(2), result2);

    }

    @Test
    public void testAbsentNotFoundHandler() {

        String exp = "$.sum($.transitModeDistance.walk, $.transitModeDistance.bicycle)";

        String fullEntry = "{\n" +
                "    \"transitModeDistance\": {\n" +
                "        \"car\": 47517.0,\n" +
                "        \"stay\": 0.0,\n" +
                "        \"walk\": 3.0,\n" +
                "        \"bicycle\": 5.0\n" +
                "    }\n" +
                "}";

        Configuration conf3 = Configuration.defaultConfiguration().setNotFoundHandler(found -> 10);
        Double result3 = (Double) JsonPath.using(conf3).parse(fullEntry).read(exp);
        assertEquals(new Double(8), result3);

        String emptyEntry = "{\n" +
                "    \"transitModeDistance\": {\n" +
                "        \"car\": 47517.0,\n" +
                "        \"stay\": 0.0\n" +
                "    }\n" +
                "}";

        Configuration conf2 = Configuration.defaultConfiguration().setNotFoundHandler(found -> 1);
        Double result2 = (Double) JsonPath.using(conf2).parse(emptyEntry).read(exp + "  ");
        assertEquals(new Double(2), result2);


    }
}
