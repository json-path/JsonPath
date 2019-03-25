package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import com.jayway.jsonpath.internal.function.numeric.Max;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ToLowerPathFunction implements PathFunction {
    @Override
    public Object invoke(String currentPath, PathRef parent, Object model, EvaluationContext ctx, List<Parameter> parameters) {
        return ((String) model).toLowerCase();
    }
}

public class FunctionDefinitionTest extends BaseFunctionTest {

    @Test
    public void testToLowerFunction() throws IllegalAccessException, InstantiationException {
        Map<String, Class> funcMap = new HashMap<String, Class>();
        funcMap.put("toLower", ToLowerPathFunction.class);
        Configuration conf = Configuration
                .builder()
                .mappingProvider(new GsonMappingProvider())
                .jsonProvider(new GsonJsonProvider())
                .functionMap(funcMap)
                .build();

        verifyFunction(conf, "$.upper.toLower()", "{\"upper\":\"UPPERCASE\"}", "uppercase");
    }

    @Test
    public void testUndefinedFunction() throws IllegalAccessException, InstantiationException {
        Map<String, Class> funcMap = new HashMap<String, Class>();
        funcMap.put("toLower", ToLowerPathFunction.class);
        Configuration conf = Configuration
                .builder()
                .mappingProvider(new GsonMappingProvider())
                .jsonProvider(new GsonJsonProvider())
                .functionMap(funcMap)
                .build();
        boolean caughtException = false;
        try {
            verifyFunction(conf, "$.upper.undefined()", "{\"upper\":\"UPPERCASE\"}", "uppercase");
        } catch (InvalidPathException exp) {
            caughtException=true;
        }
        assertThat(caughtException);
    }
}
