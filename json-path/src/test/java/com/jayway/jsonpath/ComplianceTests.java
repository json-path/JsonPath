package com.jayway.jsonpath;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * test defined in http://jsonpath.googlecode.com/svn/trunk/tests/jsonpath-test-js.html
 */
public class ComplianceTests {



    @Test
    public void test_one() throws Exception {

        String json = "{ a: \"a\",\n" +
                "           b: \"b\",\n" +
                "           \"c d\": \"e\" \n" +
                "         }";
        
        assertThat(JsonPath.<String>read(json, "$.a"), is(equalTo("a")));
        assertThat(JsonPath.<List<String>>read(json, "$.*"), hasItems("a", "b", "e"));
        assertThat(JsonPath.<List<String>>read(json, "$['*']"), hasItems("a", "b", "e"));

        //assertThat(JsonPath.<String>read(json, "$['a']"), is(equalTo("a")));              //high
        //assertThat(JsonPath.<String>read(json, "$.'c d'"), is(equalTo("e")));             //low
        //assertThat(JsonPath.<List<String>>read(json, "$[*]"), hasItems("a", "b", "e"));   //low

    }
    
    @Test
    public void test_two() throws Exception {
        String json = "[ 1, \"2\", 3.14, true, null ]";

        assertThat(JsonPath.<Integer>read(json, "$[0]"), is(equalTo(1)));
        assertThat(JsonPath.<Integer>read(json, "$[4]"), is(equalTo(null)));
        assertThat(JsonPath.<List<Comparable>>read(json, "$[*]"), hasItems(
                (Comparable)new Integer(1),
                (Comparable)new String("2"),
                (Comparable)new Double(3.14),
                (Comparable)new Boolean(true),
                null));
        assertThat(JsonPath.<Boolean>read(json, "$[-1:]"), is(equalTo(null)));
    }

    @Test
    public void test_three() throws Exception {
        String json = "{ points: [\n" +
                "             { id: \"i1\", x:  4, y: -5 },\n" +
                "             { id: \"i2\", x: -2, y:  2, z: 1 },\n" +
                "             { id: \"i3\", x:  8, y:  3 },\n" +
                "             { id: \"i4\", x: -6, y: -1 },\n" +
                "             { id: \"i5\", x:  0, y:  2, z: 1 },\n" +
                "             { id: \"i6\", x:  1, y:  4 }\n" +
                "           ]\n" +
                "         }";

        assertThat(JsonPath.<Map<String, Comparable>>read(json, "$.points[1]"), allOf(
                Matchers.<String, Comparable>hasEntry("id", "i2"),
                Matchers.<String, Comparable>hasEntry("x", -2),
                Matchers.<String, Comparable>hasEntry("y", 2),
                Matchers.<String, Comparable>hasEntry("z", 1)
        ));

        assertThat(JsonPath.<Integer>read(json, "$.points[4].x"), equalTo(0));

        assertThat(JsonPath.<List<Integer>>read(json, "$.points[?(@.id == 'i4')].x"), hasItem(-6));

        assertThat(JsonPath.<List<Integer>>read(json, "$.points[*].x"), hasItems(4, -2, 8, -6, 0, 1));

        assertThat(JsonPath.<List<String>>read(json, "$.points[?(@.z)].id"), hasItems("i2", "i5"));

        assertThat(JsonPath.<String>read(json, "$.points[(@.length-1)].id"), equalTo("i6"));

        //assertThat(JsonPath.<List<Integer>>read(json, "$['points'][?(@.x * @.x + @.y * @.y > 50)].id"), hasItems(?)); //low
    }

    @Test
    public void test_four() throws Exception {
        String json = "{ \"menu\": {\n" +
                "                 \"header\": \"SVG Viewer\",\n" +
                "                 \"items\": [\n" +
                "                     {\"id\": \"Open\"},\n" +
                "                     {\"id\": \"OpenNew\", \"label\": \"Open New\"},\n" +
                "                     null,\n" +
                "                     {\"id\": \"ZoomIn\", \"label\": \"Zoom In\"},\n" +
                "                     {\"id\": \"ZoomOut\", \"label\": \"Zoom Out\"},\n" +
                "                     {\"id\": \"OriginalView\", \"label\": \"Original View\"},\n" +
                "                     null,\n" +
                "                     {\"id\": \"Quality\"},\n" +
                "                     {\"id\": \"Pause\"},\n" +
                "                     {\"id\": \"Mute\"},\n" +
                "                     null,\n" +
                "                     {\"id\": \"Find\", \"label\": \"Find...\"},\n" +
                "                     {\"id\": \"FindAgain\", \"label\": \"Find Again\"},\n" +
                "                     {\"id\": \"Copy\"},\n" +
                "                     {\"id\": \"CopyAgain\", \"label\": \"Copy Again\"},\n" +
                "                     {\"id\": \"CopySVG\", \"label\": \"Copy SVG\"},\n" +
                "                     {\"id\": \"ViewSVG\", \"label\": \"View SVG\"},\n" +
                "                     {\"id\": \"ViewSource\", \"label\": \"View Source\"},\n" +
                "                     {\"id\": \"SaveAs\", \"label\": \"Save As\"},\n" +
                "                     null,\n" +
                "                     {\"id\": \"Help\"},\n" +
                "                     {\"id\": \"About\", \"label\": \"About Adobe CVG Viewer...\"}\n" +
                "                 ]\n" +
                "               }\n" +
                "             }";



        /*

        "$.menu.items[?(@ && @.id && !@.label)].id",
                       "$.menu.items[?(@ && @.label && /SVG/.test(@.label))].id",
                       "$.menu.items[?(!@)]",
            		   "$..[0]"
         */
    }



    /*
    --one
    { "o": { a: "a",
               b: "b",
               "c d": "e"
             },
        "p": [ "$.a",
               "$['a']",
               "$.'c d'",
               "$.*",
               "$['*']" ,
               "$[*]"
             ]
      },
      --two
      { "o": [ 1, "2", 3.14, true, null ],
        "p": [ "$[0]",
               "$[4]",
               "$[*]",
    	   "$[-1:]"
             ]
      },
      --three
      { "o": { points: [
                 { id: "i1", x:  4, y: -5 },
                 { id: "i2", x: -2, y:  2, z: 1 },
                 { id: "i3", x:  8, y:  3 },
                 { id: "i4", x: -6, y: -1 },
                 { id: "i5", x:  0, y:  2, z: 1 },
                 { id: "i6", x:  1, y:  4 }
               ]
             },
        "p": [ "$.points[1]",
               "$.points[4].x",
               "$.points[?(@.id=='i4')].x",
               "$.points[*].x",
               "$['points'][?(@.x*@.x+@.y*@.y > 50)].id",
               "$.points[?(@.z)].id",
               "$.points[(@.length-1)].id"
             ]
      },
      --four
      { "o": { "menu": {
                 "header": "SVG Viewer",
                 "items": [
                     {"id": "Open"},
                     {"id": "OpenNew", "label": "Open New"},
                     null,
                     {"id": "ZoomIn", "label": "Zoom In"},
                     {"id": "ZoomOut", "label": "Zoom Out"},
                     {"id": "OriginalView", "label": "Original View"},
                     null,
                     {"id": "Quality"},
                     {"id": "Pause"},
                     {"id": "Mute"},
                     null,
                     {"id": "Find", "label": "Find..."},
                     {"id": "FindAgain", "label": "Find Again"},
                     {"id": "Copy"},
                     {"id": "CopyAgain", "label": "Copy Again"},
                     {"id": "CopySVG", "label": "Copy SVG"},
                     {"id": "ViewSVG", "label": "View SVG"},
                     {"id": "ViewSource", "label": "View Source"},
                     {"id": "SaveAs", "label": "Save As"},
                     null,
                     {"id": "Help"},
                     {"id": "About", "label": "About Adobe CVG Viewer..."}
                 ]
               }
             },
        "p": [ "$.menu.items[?(@ && @.id && !@.label)].id",
               "$.menu.items[?(@ && @.label && /SVG/.test(@.label))].id",
               "$.menu.items[?(!@)]",
    		   "$..[0]"
             ]
      },
      --five
      { "o": { a: [1,2,3,4],
               b: [5,6,7,8]
             },
        "p": [ "$..[0]",
    	       "$..[-1:]",
    		   "$..[?(@%2==0)]"
             ]
      },
      { "o": { lin: {color:"red", x:2, y:3},
               cir: {color:"blue", x:5, y:2, r:1 },
               arc: {color:"green", x:2, y:4, r:2, phi0:30, dphi:120 },
               pnt: {x:0, y:7 }
             },
        "p": [ "$.'?(@.color)'.x",
               "$['lin','cir'].color"
             ]
      },
      { "o": { lin: {color:"red", x:2, y:3},
               cir: {color:"blue", x:5, y:2, r:1 },
               arc: {color:"green", x:2, y:4, r:2, phi0:30, dphi:120 },
               pnt: {x:0, y:7 }
             },
        "p": [ "$.'?(@.color)'.x",
               "$['lin','arc'].color"
             ]
      },
      { "o": { text: [ "hello", "world2.0"] },
        "p": [ "$.text[?(@.length > 5)]",
               "$.text[?(@.charAt(0) == 'h')]"
             ]
      },
      { "o": { a: { a:2, b:3 },
               b: { a:4, b:5 },
               c: { a: { a:6, b:7}, c:8}
             },
        "p": [ "$..a"
             ]
      },
      { "o": { a: [ { a:5, '@':2, '$':3 },   // issue 7: resolved by escaping the '@' character
                    { a:6, '@':3, '$':4 },   // in a JSONPath expression.
                    { a:7, '@':4, '$':5 }
                  ]
             },
        "p": [ "$.a[?(@['\\@']==3)]",
               "$.a[?(@['$']==5)]"
             ]
      }
     */
}
