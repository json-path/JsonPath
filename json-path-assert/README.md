json-path-assert
================

A library with [hamcrest-matchers](http://hamcrest.org/JavaHamcrest/) for JsonPath.

# Getting started

This library is available at the Central Maven Repository. Maven users add this to your POM.

```xml
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path-assert</artifactId>
    <version>2.1.0</version>
</dependency>
```

# Usage

Statically import the library entry point:

    import static com.jayway.jsonpath.matchers.JsonPathMatchers.*;

Example usage:

    // NOTE: The actual evaluation will depend on the current JsonPath configuration
    Configuration.setDefaults(...);

    // The json to be examined could be represented as a String...
    String json = ...;
    
    // a file...
    File json = ...;
    
    // or an already parsed json object...
    Object json = Configuration.defaultConfiguration().jsonProvider().parse(content);
    
    // Verify validity of JSON
    assertThat(json, isJson());

    // Verify existence of JSON path
    assertThat(json, hasJsonPath("$.message"));
    
    // Verify evaluation of JSON path
    assertThat(json, hasJsonPath("$.message", equalTo("Hi there")));
    assertThat(json, hasJsonPath("$.quantity", equalTo(5)));
    assertThat(json, hasJsonPath("$.price", equalTo(34.56)));
    assertThat(json, hasJsonPath("$.store.book[*].author", hasSize(4)));
    assertThat(json, hasJsonPath("$.store.book[*].author", hasItem("Evelyn Waugh")));
    
    // Combine several JSON path evaluations
    assertThat(json, isJson(allOf(
        withJsonPath("$.store.name", equalTo("Little Shop")),
        withJsonPath("$..title", hasSize(4)))));
        
    // Use typed matchers for increased clarity
    String json = ...
    assertThat(json, isJsonString(withJsonPath("$..author")));

    File json = ...
    assertThat(json, isJsonFile(withJsonPath("$..author")));
