package com.jayway.jsonpath.spi.json;

import java.util.Iterator;

public class JacksonIterable implements Iterable, Iterator {

    private final Iterator iterator;

    public JacksonIterable(Iterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator iterator() {
        return iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Object next() {
        return JacksonJsonNodeJsonProvider.staticUnwrap(iterator.next());
    }
}
