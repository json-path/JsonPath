Jayway JsonPath (1.0.0)
===============

**A Java DSL for reading JSON documents.**

[![Build Status](https://travis-ci.org/jayway/JsonPath.svg?branch=master)](https://travis-ci.org/jayway/JsonPath)

Jayway JsonPath is a Java port of [Stefan Goessner implementation](http://goessner.net/articles/JsonPath/). JsonPath expressions always refer to a JSON structure in the same way as XPath expression are used in combination 
with an XML document. The "root member object" in JsonPath is always referred to as `$` regardless if it is an 
object or array.

JsonPath expressions can use the dot–notation

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

Given the json

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

| JSONPath (click it to try it)| Result |
| :------- | :----- |
| <a href="http://jsonpath.herokuapp.com/?path=$.store.book[*].author" target="_blank">$.store.book[*].author</a>| The authors of all books     |
| <a href="http://jsonpath.herokuapp.com/?path=$..author" target="_blank">$..author</a>                   | All authors                         |
| <a href="http://jsonpath.herokuapp.com/?path=$.store.*" target="_blank">$.store.*</a>                  | All things, both books and bicycles  |
| <a href="http://jsonpath.herokuapp.com/?path=$.store..price" target="_blank">$.store..price</a>             | The price of everything         |
| <a href="http://jsonpath.herokuapp.com/?path=$..book[2]" target="_blank">$..book[2]</a>                 | The third book                      |
| <a href="http://jsonpath.herokuapp.com/?path=$..book[(@.length-1)]" target="_blank">$..book[(@.length-1)]</a>      | The last book            |
| <a href="http://jsonpath.herokuapp.com/?path=$..book[0,1]" target="_blank">$..book[0,1]</a>               | The first two books               |
| <a href="http://jsonpath.herokuapp.com/?path=$..book[:2]" target="_blank">$..book[:2]</a>                | All books from index 0 (inclusive) until index 2 (exclusive) |
| <a href="http://jsonpath.herokuapp.com/?path=$..book[1:2]" target="_blank">$..book[1:2]</a>                | All books from index 1 (inclusive) until index 2 (exclusive) |
| <a href="http://jsonpath.herokuapp.com/?path=$..book[-2:]" target="_blank">$..book[-2:]</a>                | Last two books                   |
| <a href="http://jsonpath.herokuapp.com/?path=$..book[2:]" target="_blank">$..book[2:]</a>                | Book number two from tail          |
| <a href="http://jsonpath.herokuapp.com/?path=$..book[?(@.isbn)]" target="_blank">$..book[?(@.isbn)]</a>          | All books with an ISBN number         |
| <a href="http://jsonpath.herokuapp.com/?path=$.store.book[?(@.price < 10)]" target="_blank">$.store.book[?(@.price < 10)]</a> | All books in store cheaper than 10  |
| <a href="http://jsonpath.herokuapp.com/?path=$..book[?(@.price <= $['expensive'])]" target="_blank">$..book[?(@.price <= $['expensive'])]</a> | All books in store that are not "expensive"  |
| <a href="http://jsonpath.herokuapp.com/?path=$..*" target="_blank">$..*</a>                        | Give me every thing                   |


Reading a document
------------------
The simplest most straight forward way to use JsonPath is via the static read API.

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

String author0 = JsonPath.read(document, "$.store.book[0].author");
String author1 = JsonPath.read(document, "$.store.book[1].author");
```
JsonPath also provides a fluent API. This is also the most flexible one.

```java
String json = "...";

ReadContext ctx = JsonPath.parse(json);

List<String> authorsOfBooksWithISBN = ctx.read("$.store.book[?(@.isbn)].author");


List<Map<String, Object>> expensiveBooks = JsonPath
                            .using(configuration)
                            .parse(json)
                            .read("$.store.book[?(@.price > 10)]", List.class);
```

All `read` operations are overloaded and also supports compiled JsonPath objects. This can be useful from a performance perspective if the same path is to be executed
many times.
   
```java
JsonPath compiledPath = JsonPath.compile("$.store.book[1].author");

String author2 = JsonPath.read(document, compiledPath);
```   


What is Returned When?
----------------------
When using JsonPath in java its important to know what type you expect in your result. Json path will automatically 
try to cast the result to the type expected by the invoker.

```java
//Will throw an java.lang.ClassCastException    
List<String> list = JsonPath.parse(json).read("$.store.book[0].author")

//Works fine
String author = JsonPath.parse(json).read("$.store.book[0].author")
```

When evaluating a path you need to understand the concept of when a path is `definite`. A path is `indefinite` if it contains:

* `..` - a deep scan operator
* `?(<expression>)` - an expression
* `[<number>, <number> (, <number>)]` - multiple array indexes
* `['<name>', '<name>' (, '<name>')]` - multiple object properties

`Indefinite` paths always returns a list. 

By default some simple object mapping is provided by the MappingProvider SPI. This allows to specify the return type you want and the MappingProvider will
try to perform the mapping. If a book, in the sample json above,  had a long value 'published' you could perform object mapping between `Long` and `Date`
as shown below. 

```java
String json = "{\"date_as_long\" : 1411455611975}";

Date date = JsonPath.parse(json).read("$['date_as_long']", Date.class);
```

If you configure JsonPath to use the `JacksonMappingProvider` you can even map your JsonPath output directly into POJO's.

```java
Book book = JsonPath.parse(json).read("$.store.book[0]", Book.class);
```

Predicates
----------
There are three different ways to create filter predicates in JsonPath.

###Inline predicates

Inline predicates are the ones defined in the path.

```java
List<Map<String, Object>> books =  JsonPath.parse(json).read("$.store.book[?(@.price < 10)]");
```

In the current implementation you can use `&&` to combine multiple predicates `[?(@.price < 10 && @.category == 'fiction')]`. OR operations are not supported yet.
 
###The Filter API
 
Predicates can be built using the Filter API as shown below:

```java
import static com.jayway.jsonpath.JsonPath.parse;
import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;
...
...

Filter cheapFictionFilter = filter(where("category").is("fiction").and("price").lte(10D));

List<Map<String, Object>> books =  parse(json).read("$.store.book[?]", cheapFictionFilter);

```
Note the placeholder '?' for the filter in the path. When multiple filters are provided they are applied in order where the number of placeholders must match 
the number of provided filters. You can specify multiple predicate placeholders in one filter operation `[?, ?]`, both predicates must match. 

###Roll your own
 
Third option is to implement your own predicates
 
```java 
Predicate booksWithISBN = new Predicate() {
    @Override
    public boolean apply(PredicateContext ctx) {
        return ctx.item(Map.class).containsKey("isbn");
    }
};

List<Map<String, Object>> books = reader.read("$.store.book[?].isbn", List.class, booksWithISBN);
```

PATH vs VALUE
-------------
As specified in the Goessner implementation a JsonPath can return either `Path` or `Value`. `Value` is the default and what all the exaples above are reuturning. If you rather have the path of the elements our query is hitting this can be acheived with an option.

```java
Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();

List<String> pathList = using(conf).parse(JSON_DOCUMENT).read("$..author");

assertThat(pathList).containsExactly(
    "$['store']['book'][0]['author']",
    "$['store']['book'][1]['author']",
    "$['store']['book'][2]['author']",
    "$['store']['book'][3]['author']");
```


Tweaking Configuration
----------------------

JsonPath is shipped with three different JsonProviders:

* [JsonSmartJsonProvider](https://code.google.com/p/json-smart/) (default)
* [JacksonJsonProvider](https://github.com/FasterXML/jackson)
* [GsonJsonProvider](https://code.google.com/p/google-gson/) (experimental)

Changing the configuration defaults as demonstrated should only be done when your application is being initialized. Changes during runtime is strongly discouraged, especially in multi threaded applications.  

```java
Configuration.setDefaults(new Configuration.Defaults() {

    private final JsonProvider jsonProvider = new JacksonJsonProvider();
      
    @Override
    public JsonProvider jsonProvider() {
        return jsonProvider;
    }

    @Override
    public MappingProvider mappingProvider() {
        return new JacksonMappingProvider();
    }
    
    @Override
    public Set<Option> options() {
        return EnumSet.noneOf(Option.class);
    }
});
```

Note that the JacksonJsonProvider requires `com.fasterxml.jackson.core:jackson-databind:2.4.1.3` and the GsonJsonProvider requires `com.google.code.gson:gson:2.3` on your classpath. 

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
