package com.blogspot.sontx.jini;

import java.util.HashMap;

/**
 * Copyright 2016 by sontx
 * Created by sontx on 21/4/2016.
 */
public class INIMemory {
    private HashMap<String, String> hashMap = new HashMap<>();

    public boolean contains(String key) {
        return hashMap.containsKey(key);
    }

    public void put(String key, String value) {
        synchronized (this) {
            hashMap.put(key, value);
        }
    }

    public void remove(String key) {
        synchronized (this) {
            hashMap.remove(key);
        }
    }

    public String get(String key) {
        return hashMap.get(key);
    }

    public void clear() {
        synchronized (this) {
            hashMap.clear();
        }
    }

    public Iterable<String> getKeys() {
        return hashMap.keySet();
    }
}
