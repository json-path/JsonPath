package com.jayway.jsonpath;

import org.junit.Assert;
import org.junit.Test;

/**
 * test for issue 771
 */
public class Issue_771 {
    @Test
    public void testReverseExpression1(){
        String in="{\"authors\":[{\"first\":[1,2],\"last\":2}]}";
        String out =JsonPath.parse(in).read("$.authors[?([1,2]==@.first)]").toString();
        Assert.assertEquals(out,"[{\"first\":[1,2],\"last\":2}]");
    }
    @Test
    public void testReverseExpression2(){
        String in="{\n" +
                "\"store\": {\n" +
                "\"book\": [\n" +
                "{\n" +
                "\"category\": \"reference\",\n" +
                "\"authors\": [\n" +
                "{\n" +
                "\"firstName\": \"Nigel\",\n" +
                "\"lastName\": \"Rees\"\n" +
                "},\n" +
                "{\n" +
                "\"firstName\": \"Evelyn\",\n" +
                "\"lastName\": \"Waugh\"\n" +
                "}\n" +
                "],\n" +
                "\"title\": \"Sayings of the Century\",\n" +
                "\"price\": 8.95\n" +
                "},\n" +
                "{\n" +
                "\"category\": \"fiction\",\n" +
                "\"authors\": [\n" +
                "{\n" +
                "\"firstName\": \"A\",\n" +
                "\"lastName\": \"B\"\n" +
                "},\n" +
                "{\n" +
                "\"firstName\": \"C\",\n" +
                "\"lastName\": \"D\"\n" +
                "}\n" +
                "],\n" +
                "\"title\": \"Sword of Honour\",\n" +
                "\"price\": 12.99\n" +
                "}\n" +
                "]\n" +
                "},\n" +
                "\"expensive\": 10\n" +
                "}";
        String out =JsonPath.parse(in).read("$.store.book[?(['Rees','Waugh'] == @.authors[*].lastName)]").toString();
        Assert.assertEquals(out,"[{\"category\":\"reference\",\"authors\":[{\"firstName\":\"Nigel\",\"lastName\":\"Rees\"},{\"firstName\":\"Evelyn\",\"lastName\":\"Waugh\"}],\"title\":\"Sayings of the Century\",\"price\":8.95}]\n");
    }
}
