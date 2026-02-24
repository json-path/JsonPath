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

package com.jayway.jsonpath.spi.cache;

import com.jayway.jsonpath.JsonPath;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class LRUCache implements Cache {

  private static final float DEFAULT_LOAD_FACTOR = 0.75f;
  private final int size;
  private LinkedHashMap<String, JsonPath> hashMap;

  public LRUCache(int size) {
    this.size = size;
    int capacity = (int) Math.ceil(size / DEFAULT_LOAD_FACTOR) + 1;
    hashMap = new LinkedHashMap<String, JsonPath>(capacity, DEFAULT_LOAD_FACTOR, true) {
      // (an anonymous inner class)
      private static final long serialVersionUID = 1;

      @Override
      protected boolean removeEldestEntry(Map.Entry<String, JsonPath> eldest) {
        return size() > LRUCache.this.size;
      }
    };
  }

  public void put(String key, JsonPath value) {
    synchronized (this) {
      hashMap.put(key, value);
    }
  }

  public JsonPath get(String key) {
    synchronized (this) {
      return hashMap.get(key);
    }
  }

  public JsonPath getSilent(String key) {
    synchronized (this) {
      return hashMap.get(key);
    }
  }

  public int size() {
    synchronized (this) {
      return hashMap.size();
    }
  }

  public String toString() {
    synchronized (this) {
      return hashMap.toString();
    }
  }
}
