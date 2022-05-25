package com.jayway.jsonpath;

import org.junit.Assert;
import org.junit.Test;

public class Issue_411 {
    @Test
    public void TestIndex1(){
        String json="{\n" +
                "\"description\":\"out\",\n" +
                "\"inner\":{\n" +
                "\"description\":\"inner\"\n" +
                "}\n" +
                "}";
        Object result=JsonPath.parse(json).read("$..description[1]");
        Assert.assertEquals("[\"inner\"]",result.toString());
    }
    @Test
    public void TestIndex2(){
        String json="{\n" +
                "\"description\":[\"1\",\"2\"],\n" +
                "\"map\":\"go\",\n" +
                "\"inner\":{\n" +
                "\"description\":\"inner\"\n" +
                "}\n" +
                "}";
        Object result=JsonPath.parse(json).read("$..description[0]");
//        System.out.println(result);
        Assert.assertEquals("[[\"1\",\"2\"]]",result.toString());
    }
}
