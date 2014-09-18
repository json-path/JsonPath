JSONPath
========

**A Java DSL for reading JSON documents.**

[![Build Status](https://travis-ci.org/jayway/JsonPath.svg?branch=master)](https://travis-ci.org/jayway/JsonPath)

JSONPath expressions always refer to a JSON structure in the same way as XPath expression are used in combination 
with an XML document. The "root member object" in JSONPath is always referred to as `$` regardless if it is an 
object or array.

JSONPath expressions can use the dot–notation

`$.store.book[0].title`

or the bracket–notation

`$['store']['book'][0]['title']`

Operators
---------

| Operator                  | Description                                                        |
| :------------------------ | :----------------------------------------------------------------- |
| `$`                       | The root element to query. This starts all path expressions.       |
| `@`                       | The current node being processed by a filter predicate.            |
| `*`                       | Wildcard. Available anywhere a name or numeric are required.       |
| `..`                      | Deep scan. Available anywhere a name is required.                  |
| `.<name>`                 | Dot-notated child                                                  |
| `['<name>' (, '<name>')]` | Bracket-notated child or children                                  |
| `[<number> (, <number>)]` | Array index or indexes                                             |
| `[start:end]`             | Array slice operator                                               |
| `[?(<expression>)]`       | Filter expression. Expression must evaluate to a boolean value.    |

Path Examples
-------------

Given the 

```javascript
{
    "store": {
        "book": [
            {
                "category": "reference",
                "author": "Nigel Rees",
                "title": "Sayings of the Century",
                "price": 8.95
            },
            {
                "category": "fiction",
                "author": "Evelyn Waugh",
                "title": "Sword of Honour",
                "price": 12.99
            },
            {
                "category": "fiction",
                "author": "Herman Melville",
                "title": "Moby Dick",
                "isbn": "0-553-21311-3",
                "price": 8.99
            },
            {
                "category": "fiction",
                "author": "J. R. R. Tolkien",
                "title": "The Lord of the Rings",
                "isbn": "0-395-19395-8",
                "price": 22.99
            }
        ],
        "bicycle": {
            "color": "red",
            "price": 19.95
        }
    },
    "expensive": 10
}
```

| JSONPath | Result |
| :------- | :----- |
| `$.store.book[*].author`      | The authors of all books            |
| `$..author`                   | All authors                         |
| `$.store.*`                   | All things, both books and bicycles |
| `$.store..price`              | The price of everything             |
| `$..book[2]`                  | The third book                      |
| `$..book[(@.length-1)]`       | The last book                       |
| `$..book[0,1]`                | The first two books                 |
| `$..book[:2]`                 | All books from index 0 (inclusive) until index 2 (exclusive) |
| `$..book[1:2]`                | All books from index 1 (inclusive) until index 2 (exclusive) |
| `$..book[-2:]`                | Last two books                      |
| `$..book[2:]`                 | Book number two from tail           |
| `$..book[?(@.isbn)]`          | All books with an ISBN number       |
| `$.store.book[?(@.price < 10)]` | All books in store cheaper than 10  |
| `$..book[?(@.price <= $['expensive'])]` | All books in store that are not "expensive"  |
| `$..*`                        | Give me every thing you got         |


Reading a document
------------------
The simplest most straight forward way to use JsonPath is via the static convenience API.

```java
String json = "...";

List<String> authors = JsonPath.read(json, "$.store.book[*].author");
```

If you only want to read once this is OK. In case you need to read an other path as well this is not the way 
to go since the document will be parsed every time you call JsonPath.read(...). To avoid the problem you can 
parse the json first.

```java
String json = "...";
Object document = Configuration.defaultConfiguration().jsonProvider().parse(json);

String author1 = JsonPath.read(document, "$.store.book[0].author");
String author2 = JsonPath.read(document, "$.store.book[1].author");
```
Personally I prefer the more flexible `ReadContext` API.

```java
String json = "...";

ReadContext ctx = JsonPath.parse(json);

List<String> authorsOfBooksWithISBN = ctx.read("$.store.book[?(@.isbn)].author");


List<Map<String, Object>> expensiveBooks = JsonPath
                            .using(configuration)
                            .parse(json)
                            .read("$.store.book[?(@.price > 10)]", List.class);
```

What is Returned When?
----------------------

Predicates
----------

PATH vs VALUE
-------------

Tweaking Configuration
----------------------

The default JsonProvider is `JsonSmartJsonProvider` backed by [json-smart](https://code.google.com/p/json-smart/), a small and fast JSONParser. If you
prefer Jackson there is a `JacksonJsonProvider` available. There is also an experimental `GsonJsonProvider`. 

* JacksonJsonProvider requires `com.fasterxml.jackson.core:jackson-databind:2.4.1.3` on your classpath. 
* GsonJsonProvider requires `com.google.code.gson:gson:2.3` on your classpath. 

```java
Configuration.setDefaults(new Configuration.Defaults() {

    private final JsonProvider jsonProvider = new com.jayway.jsonpath.internal.spi.json.JacksonJsonProvider();
      
    @Override
    public JsonProvider jsonProvider() {
        return jsonProvider;
    }

    @Override
    public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
    }

    @Override
    public ConversionProvider conversionProvider() {
        return new DefaultConversionProvider();
    }
});
```

Binaries
--------

JsonPath is available at the Central Maven Repository. Maven users add this to your POM.

```xml
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <version>0.9.1</version>
</dependency>
```

Gradle users
 
 
```
compile 'com.jayway.jsonpath:json-path:0.9.1'
``` 

[![Analytics](https://ga-beacon.appspot.com/UA-54945131-1/jsonpath/index)](https://github.com/igrigorik/ga-beacon)
