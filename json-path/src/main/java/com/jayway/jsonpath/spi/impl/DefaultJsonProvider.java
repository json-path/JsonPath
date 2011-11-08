package com.jayway.jsonpath.spi.impl;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.JsonProvider;
import com.jayway.jsonpath.spi.Mode;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: kallestenflo
 * Date: 11/8/11
 * Time: 3:56 PM
 */
public class DefaultJsonProvider extends JsonProvider {

    private Mode mode;

    private JSONParser parser;

    public DefaultJsonProvider() {
        this(Mode.SLACK);
    }

    public DefaultJsonProvider(Mode mode) {
        this.mode = mode;
        this.parser = new JSONParser(mode.intValue());
    }

    public Map<String, Object> createMap() {
        return new JSONObject();
    }

    public List<Object> createList() {
        return new JSONArray();
    }

    public Object parse(String json) {
        try {
            return parser.parse(json);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        } catch (IOException e) {
            throw new InvalidJsonException(e);
        }
    }

    public Mode getMode() {
        return mode;
    }
}
