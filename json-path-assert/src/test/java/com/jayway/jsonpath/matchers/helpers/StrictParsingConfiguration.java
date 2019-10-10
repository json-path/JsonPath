package com.jayway.jsonpath.matchers.helpers;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.mapper.JsonSmartMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import com.jayway.jsonpath.spi.transformer.TransformationProvider;
import com.jayway.jsonpath.spi.transformer.TransformationSpec;
import net.minidev.json.parser.JSONParser;

import java.io.InputStream;
import java.util.EnumSet;
import java.util.Set;

public class StrictParsingConfiguration implements Configuration.Defaults {

    private final JsonProvider jsonProvider = new JsonSmartJsonProvider(JSONParser.MODE_STRICTEST);
    private final MappingProvider mappingProvider = new JsonSmartMappingProvider();

    public JsonProvider jsonProvider() {
        return jsonProvider;
    }

    public MappingProvider mappingProvider() {
        return mappingProvider;
    }

    @Override
    public TransformationProvider transformationProvider() {
        return new TransformationProvider() {
            @Override
            public TransformationSpec spec(String input, Configuration configuration) {
                return null;
            }

            @Override
            public TransformationSpec spec(InputStream input, Configuration configuration) {
                return null;
            }

            @Override
            public Object transform(Object source, TransformationSpec spec, Configuration configuration) {
                return null;
            }
        };
    }

    public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
    }
}
