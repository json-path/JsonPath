/*
 * Copyright 2011 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jayway.jsonpath.internal;

import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;

public class Cache {

    /**
     * Wrapped JCache.
     */
    private final javax.cache.Cache<String, Path> jcache;

    public Cache() {
        javax.cache.Cache<String, Path> existingJcache = Caching.getCache("jsonpath", String.class, Path.class);
        if (existingJcache == null) {
            final MutableConfiguration<String, Path> configuration = new MutableConfiguration<String, Path>();
            configuration.setTypes(String.class, Path.class);
            configuration.setStoreByValue(false);
            existingJcache = Caching.getCachingProvider().getCacheManager().createCache("jsonpath", configuration);
        }
        jcache = existingJcache;
    }

    public void put(String key, Path value) {
        jcache.put(key,value);
    }

    public Path get(String key) {
        return jcache.get(key);
    }

    public Path getSilent(String key) {
        return get(key);
    }

    public void remove(String key) {
        jcache.remove(key);
    }

}