package com.jayway.jsonpath.internal.function;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configurations;
import com.jayway.jsonpath.internal.EvaluationContext;
import com.jayway.jsonpath.internal.PathRef;
import org.junit.jupiter.api.Test;

import java.io.InvalidClassException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomFunctionTest extends BaseFunctionTest {

    private Configuration conf = Configurations.JSON_SMART_CONFIGURATION;

    public class InvalidFunction {

    }
    @Test
    void testAddInvalidFunction(){
        assertThrows(InvalidClassException.class, () -> PathFunctionFactory.addCustomFunction("invalid",
                InvalidFunction.class));
    }

    @Test
    void testAddValidFunction(){
       assertDoesNotThrow(()->PathFunctionFactory.addCustomFunction("toUpperCase",ToUpperCase.class));
       verifyTextFunction(conf,"$['text'][0].toUpperCase()","A");
    }
}
