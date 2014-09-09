package com.jayway.jsonpath.internal;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class Cache {

    private final ReentrantLock lock = new ReentrantLock();


    private final Map<String, Path> map = new ConcurrentHashMap<String, Path>();
    private final Deque<String> queue = new LinkedList<String>();
    private final int limit;


    public Cache(int limit) {
        this.limit = limit;
    }

    public void put(String key, Path value) {
        Path oldValue = map.put(key, value);
        if (oldValue != null) {
            removeThenAddKey(key);
        } else {
            addKey(key);
        }
        if (map.size() > limit) {
            map.remove(removeLast());
        }
    }

    public Path get(String key) {
        removeThenAddKey(key);
        return map.get(key);
    }

    private void addKey(String key) {
        lock.lock();
        try {
            queue.addFirst(key);
        } finally {
            lock.unlock();
        }


    }

    private String removeLast() {
        lock.lock();
        try {
            final String removedKey = queue.removeLast();
            return removedKey;
        } finally {
            lock.unlock();
        }
    }

    private void removeThenAddKey(String key) {
        lock.lock();
        try {
            queue.removeFirstOccurrence(key);
            queue.addFirst(key);
        } finally {
            lock.unlock();
        }

    }

    private void removeFirstOccurrence(String key) {
        lock.lock();
        try {
            queue.removeFirstOccurrence(key);
        } finally {
            lock.unlock();
        }

    }


    public Path getSilent(String key) {
        return map.get(key);
    }

    public void remove(String key) {
        removeFirstOccurrence(key);
        map.remove(key);
    }

    public int size() {
        return map.size();
    }

    public String toString() {
        return map.toString();
    }
}