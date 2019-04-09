package com.jayway.jsonpath.spi.json;

public interface JsonWrapper<O> extends Iterable{

   O unwrap();
   
   int size();
}
