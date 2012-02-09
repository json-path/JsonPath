package com.jayway.jsonpath.spi.impl;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.Mode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 10:34 PM
 */
public class JacksonProvider extends JsonProvider {
    @Override
    public Mode getMode() {
        return Mode.STRICT;
    }

    @Override
    public Object parse(String json) throws InvalidJsonException {
        try {
            return new ObjectMapper().readValue(json, Object.class);
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public String toJson(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> createMap() {
        return new HashMap<String, Object>();
    }

    @Override
    public List<Object> createList() {
        return new LinkedList<Object>();
    }
}
