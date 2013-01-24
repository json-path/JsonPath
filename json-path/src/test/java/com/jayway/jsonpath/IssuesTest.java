package com.jayway.jsonpath;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.jayway.jsonpath.JsonPath.compile;
import static com.jayway.jsonpath.JsonPath.read;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 2/29/12
 * Time: 8:42 AM
 */
public class IssuesTest {
    private static final Logger logger = LoggerFactory.getLogger(IssuesTest.class);

    @Test
    public void issue_7() throws Exception {

        String json = "{ \"foo\" : [\n" +
                "  { \"id\": 1 },  \n" +
                "  { \"id\": 2 },  \n" +
                "  { \"id\": 3 }\n" +
                "  ] }";


        assertNull(read(json, "$.foo.id"));
    }

    @Test
    public void issue_11() throws Exception {
        String json = "{ \"foo\" : [] }";
        List<String> result = read(json, "$.foo[?(@.rel= 'item')][0].uri");

        logger.debug("{}", compile("$.foo[?(@.rel= 'item')][0].uri").isPathDefinite());
        logger.debug("{}", compile("$.foo[?(@.rel= 'item')][0]").isPathDefinite());
        logger.debug("{}", compile("$.foo[?(@.rel= 'item')]").isPathDefinite());

        assertTrue(result.isEmpty());
    }

}
