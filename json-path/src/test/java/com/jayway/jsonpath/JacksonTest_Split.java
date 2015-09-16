package com.jayway.jsonpath;

import static org.junit.Assert.*;
import com.fasterxml.jackson.core.JsonFactory;
import com.jayway.jsonpath.internal.Path;
import com.jayway.jsonpath.internal.PathCompiler;
import com.jayway.jsonpath.internal.token.TokenStack;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;

public class JacksonTest_Split extends BaseTest implements EvaluationCallback {
    
    private static final Logger log = LoggerFactory.getLogger(JacksonTest_Split.class);
    private List<Object> results = new ArrayList<Object>();
    
    @Test
    public void jsonTest() throws Exception {
        String res = "json_opsview1.json";
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(res)) {
             Path path = PathCompiler.compile("$.list[*]");
             
             TokenStack stack = new TokenStack(JACKSON_CONFIGURATION);
           
             JsonFactory factory = new JsonFactory();
             stack.registerPath(path);
             stack.read(factory.createParser(stream), this, false);
        }
        log.debug("results: " + results.size());
        assertTrue(results.size() == 96);
    }

    @Override
    public void resultFound(Object source, Object obj, Path path) throws Exception {
        //log.debug(source + ":" + String.valueOf(obj));
        results.add(obj);
    }

    @Override
    public void resultFoundExit(Object source, Object obj, Path path) throws Exception {
        //log.debug(source + ":" + String.valueOf(obj));
    }
}
