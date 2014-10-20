In The Pipe
===========
* Added EvaluationListener interface that allows abortion of evaluation if criteria is fulfilled.
  this makes it possible to limit the number of results to fetch when a document is scanned. Also 
  added utility method to limit results `JsonPath.parse(json).limit(1).read("$..title", List.class);`
* Added support for OR in inline filters [?(@.foo == 'bar' || @.foo == 'baz')] 
* Upgrade json-smart to 2.1.0


1.1.0 (2014-10-01)
==================
* Reintroduced method JsonProvider.createMap(). This should never have been removed. **NOTE: This is a breaking change if you implemented your own JsonProvider based on the 1.0.0 API**  
* Filters threw exception if an item being filtered did not contain the path being filtered upon.
* Multi-property selects works as it did in 0.9. e.g. `$[*]['category', 'price']` 
* Cache results when predicates refer to path in document e.g. `$[*][?(@.price <= $.max-price)]` will only evaluate `$.max-price` once.   

1.0.0 (2014-09-26)
==================
* Complete rewrite of internals. Major API changes.
* Better compliance with the Goessner implementation

Release history
===============
* 0.9.0 (2013-09-26)
* 0.8.1 (2012-04-16)
* 0.8.0 (2012-03-08)
* 0.5.6 (2012-02-09)
* 0.5.5 (2011-07-15)
* 0.5.4 (2011-06-26)
* 0.5.3 (2011-02-18)
* 0.5.2 (2011-02-08)


