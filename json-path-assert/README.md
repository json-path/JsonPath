json-path-assert
================

A library with [hamcrest-matchers](http://hamcrest.org/JavaHamcrest/) for JsonPath.

# Getting started

This library is available at the Central Maven Repository. Maven users add this to your POM.

```xml
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path-assert</artifactId>
    <version>2.2.0</version>
</dependency>
```

# Usage guide

Statically import the library entry point:

    import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;

NOTE: The actual evaluation of JsonPath will depend on the current configuration:

    Configuration.setDefaults(...);

The matchers can be used to inspect different representations of JSON:

    // As a String...
    String json = ...;
    
    // or a file...
    File json = ...;
    
    // or an already parsed json object...
    Object json = Configuration.defaultConfiguration().jsonProvider().parse(content);
    
Usage examples:
    
    // Verify validity of JSON
    assertThat(json, isJson());

    // Verify existence (or non-existence) of JSON path
    assertThat(json, hasJsonPath("$.message"));
    assertThat(json, hasNoJsonPath("$.message"));

    // Verify evaluation of JSON path
    assertThat(json, hasJsonPath("$.message", equalTo("Hi there")));
    assertThat(json, hasJsonPath("$.quantity", equalTo(5)));
    assertThat(json, hasJsonPath("$.price", equalTo(34.56)));
    assertThat(json, hasJsonPath("$.store.book[*].author", hasSize(4)));
    assertThat(json, hasJsonPath("$.store.book[*].author", hasItem("Evelyn Waugh")));

Combine matchers for greater expressiveness
    
    // This will separate the JSON parsing from the path evaluation
    assertThat(json, isJson(withoutJsonPath("...")));
    assertThat(json, isJson(withJsonPath("...", equalTo(3))));    
    
    // Combine several JSON path evaluations into a single statement
    // (This will parse the JSON only once)
    assertThat(json, isJson(allOf(
        withJsonPath("$.store.name", equalTo("Little Shop")),
        withoutJsonPath("$.expensive"),
        withJsonPath("$..title", hasSize(4)))));

Match on pre-compiled complex JSON path expressions

    Filter cheapFictionFilter = filter(
        where("category").is("fiction").and("price").lte(10D));
    JsonPath cheapFiction = JsonPath.compile("$.store.book[?]", cheapFictionFilter);
    String json = ...;
    assertThat(json, isJson(withJsonPath(cheapFiction)));
        
Use typed matchers for specific JSON representations, if needed

    String json = ...
    assertThat(json, isJsonString(withJsonPath("$..author")));

    File json = ...
    assertThat(json, isJsonFile(withJsonPath("$..author")));
