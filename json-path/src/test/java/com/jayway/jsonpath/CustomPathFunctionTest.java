package com.jayway.jsonpath;

import com.jayway.jsonpath.internal.function.BaseFunctionTest;
import com.jayway.jsonpath.spi.pathFunction.DefaultPathFunctionProvider;
import com.jayway.jsonpath.spi.pathFunction.PathFunction;
import com.jayway.jsonpath.spi.pathFunction.PathFunctionProvider;
import org.junit.jupiter.api.Test;

import static com.jayway.jsonpath.Configurations.JSON_ORG_CONFIGURATION;
import static com.jayway.jsonpath.JsonPath.using;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomPathFunctionTest extends BaseFunctionTest {

    @Test
    void testCustomPathFunction() {
        Configuration conf = Configuration.defaultConfiguration().pathFunctionProvider(new DefaultPathFunctionProvider() {
            @Override
            public PathFunction newFunction(String name) throws InvalidPathException {
                if (name.equals("toUpperCase")) {
                    return (currentPath, parent, model, ctx, parameters) -> ((String) model).toUpperCase();
                }
                return super.newFunction(name);
            }
        });
        verifyTextFunction(conf,"$['text'][0].toUpperCase()","A");

        // Make sure default configuration is not being overwritten
        assertThrows(InvalidPathException.class, () -> {
            using(JSON_ORG_CONFIGURATION).parse(TEXT_SERIES).read("$['text'][0].toUpperCase()");
        });
    }

    @Test
    void removeAllPathFunctions() {
        Configuration conf = Configuration.defaultConfiguration().pathFunctionProvider(name -> {
            throw new InvalidPathException("No JsonPath functions allowed");
        });
        verifyMathFunction(JSON_ORG_CONFIGURATION, "$['empty'].length()", 0);
        assertThrows(InvalidPathException.class, () -> {
            using(conf).parse(NUMBER_SERIES).read("$['empty'].length()");
        });
    }
}
