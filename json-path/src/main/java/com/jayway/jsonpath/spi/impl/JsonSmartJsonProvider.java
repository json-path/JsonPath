/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.jsonpath.spi.impl;

import com.jayway.jsonpath.InvalidJsonException;
import com.jayway.jsonpath.spi.Mode;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.ContainerFactory;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

/**
 * @author Kalle Stenflo
 */
public class JsonSmartJsonProvider extends AbstractJsonProvider {

    private Mode mode;

    private JSONParser parser;

    private ContainerFactory containerFactory = ContainerFactory.FACTORY_SIMPLE;

    public JsonSmartJsonProvider() {
        this(Mode.SLACK);
    }

    public JsonSmartJsonProvider(Mode mode) {
        this.mode = mode;
        this.parser = new JSONParser(mode.intValue());
    }

    public Map<String, Object> createMap() {
        return containerFactory.createObjectContainer();
    }

    public List<Object> createList() {
        return containerFactory.createArrayContainer();
    }

    public Object parse(String json) {
        try {
            //return parser.parse(json, ContainerFactory.FACTORY_ORDERED);
            return parser.parse(json, containerFactory);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(Reader jsonReader) throws InvalidJsonException {
        try {
            return parser.parse(jsonReader, containerFactory);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public Object parse(InputStream jsonStream) throws InvalidJsonException {
        try {
            return parser.parse(new InputStreamReader(jsonStream), containerFactory);
        } catch (ParseException e) {
            throw new InvalidJsonException(e);
        }
    }

    @Override
    public String toJson(Object obj) {

        if(obj instanceof Map) {
            return JSONObject.toJSONString((Map<String, ?>) obj);
        } else if(obj instanceof List){
            return JSONArray.toJSONString((List<?>) obj);
        } else {
            throw new UnsupportedOperationException(obj.getClass().getName() + " can not be converted to JSON");
        }
    }

    public Mode getMode() {
        return mode;
    }
}
