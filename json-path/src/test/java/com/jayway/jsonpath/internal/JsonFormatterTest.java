package com.jayway.jsonpath.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class JsonFormatterTest {

    // Test case for pretty printing a simple JSON object
    // The input is a simple JSON object
    // The expected output is the same JSON object with proper indentation
    @Test
    public void testPrettyPrint_SimpleJson() {
        JsonFormatter jsonFormatter = new JsonFormatter();
        String input = "{\"name\":\"Vaibhav Ramchandani\",\"age\":23,\"city\":\"Halifax\"}";
        String actualOutput = jsonFormatter.prettyPrint(input);
        String expectedOutput = "{\n   \"name\" : \"Vaibhav Ramchandani\",\n   \"age\" : 23,\n   \"city\" : \"Halifax\"\n}";
        assertEquals(expectedOutput.replaceAll("\\s", ""), actualOutput.replaceAll("\\s", ""));
    }

    //  Test case for pretty printing a JSON object with escaped characters
    // The input is a JSON object with escaped characters
    // The expected output is the same JSON object with proper indentation
  //
    @Test
    public void testPrettyPrint_JsonWithEscapedCharacters() {
        JsonFormatter jsonFormatter = new JsonFormatter();
        String input = "{\"message\":\"This is ,\\nJsonPath Repo!\"}";
        String actualOutput = jsonFormatter.prettyPrint(input);
        String expectedOutput = "{\n   \"message\" : \"This is ,\\nJsonPath Repo!\"\n}";
        assertEquals(expectedOutput.replaceAll("\\s", ""), actualOutput.replaceAll("\\s", ""));
    }

    // Test case for pretty printing a JSON array
    // The input is a JSON array
    // The expected output is the same JSON array with proper indentation
    @Test
    public void testPrettyPrint_JsonWithArray() {
        //
        JsonFormatter jsonFormatter = new JsonFormatter();
        String input = "[{\"name\":\"Vaibhav R\",\"age\":23},{\"name\":\"Sanskar K\",\"age\":24}]";
        String actualOutput=jsonFormatter.prettyPrint(input);
        String expectedOutput = "[\n   {\n      \"name\" : \"Vaibhav R\",\n      \"age\" : 23\n   },\n   {\n      \"name\" : \"Sanskar K\",\n      \"age\" : 24\n   }\n]";
        assertEquals(expectedOutput.replaceAll("\\s", ""), actualOutput.replaceAll("\\s", ""));
    }

    // Test case for pretty printing a JSON object with single quotes
    // The input is a JSON object with single quotes
    // The expected output is the same JSON object with proper indentation
    @Test
    public void testPrettyPrint_JsonWithSingleQuotes() {

        JsonFormatter jsonFormatter = new JsonFormatter();
        String input = "{'name':'Vaibhav Ramchandani','age':23,'city':'Halifax'}";
        String actualOutput = jsonFormatter.prettyPrint(input);
        String expectedOutput = "{\n   'name' : 'Vaibhav Ramchandani',\n   'age' : 23,\n   'city' : 'Halifax'\n}";
        assertEquals(expectedOutput.replaceAll("\\s", ""), actualOutput.replaceAll("\\s", ""));
    }
}

