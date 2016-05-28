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

---

### Regarding the use of indefinite paths

When using indefinite path expressions (e.g with wildcards '*'), the result will yield a list. Possibly an _empty_ list if no matching entries were found. If you want to assert that the list will actually contain something, make sure to express this explicitly, e.g checking for the size of the list.

    // Given a JSON like this:
    {
      "items": []
    }

    // Both of these statements will succeed(!)
    assertThat(json, hasJsonPath("$.items[*]"));
    assertThat(json, hasJsonPath("$.items[*].name"));
    
    // Make sure to explicitly check for size if you want to catch this scenario as a failure
    assertThat(json, hasJsonPath("$.items[*]", hasSize(greaterThan(0))));
    
    // However, checking for the existence of an array works fine, as is
    assertThat(json, hasJsonPath("$.not_here[*]"));

---

### Regarding the use of null in JSON

'null' is a valid JSON value. If such a value exist, the path is still considered to be a valid path.

    // Given a JSON like this:
    { "none": null }
    
    // All of these will succeed, since '$.none' is a valid path
    assertThat(json, hasJsonPath("$.none"));
    assertThat(json, isJson(withJsonPath("$.none")));
    assertThat(json, hasJsonPath("$.none", nullValue()));
    assertThat(json, isJson(withJsonPath("$.none", nullValue())));
    
    // But all of these will fail, since '$.not_there' is not a valid path
    assertThat(json, hasJsonPath("$.not_there"));
    assertThat(json, isJson(withJsonPath("$.not_there")));
    assertThat(json, hasJsonPath("$.not_there", anything()));
    assertThat(json, isJson(withJsonPath("$.not_there", anything())));

