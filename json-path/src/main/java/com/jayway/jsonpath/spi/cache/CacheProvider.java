package com.jayway.jsonpath.spi.cache;

import com.jayway.jsonpath.JsonPathException;

public class CacheProvider {
    private static Cache cache = new LRUCache(200);

    public static void setCache(Cache cache){
        if (cache != null){
            CacheProvider.cache = cache;
        }
    }

    public static Cache getCache() {
        try {
            return cache;
        } catch (Exception e) {
            throw new JsonPathException("Failed to get cache", e);
        }
    }
}