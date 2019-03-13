package com.example.george.bookmarker.model;

import android.util.LruCache;

/**
 * Created by GEORGE on 2017/04/23.
 */

public class ImageCache {

    static {
        int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 5;
        cache = new LruCache<>(cacheSize);
    }

    private static LruCache<Long, byte[]> cache;

    public static void remove(long key) {
        cache.remove(key);
    }

    public static void put(Long key, byte[] data){
        synchronized (cache) {
            if (cache.get(key) == null) {
                cache.put(key, data);
            }
        }
    }

    public static byte[] get(Long key){
        if(key != null){
            return cache.get(key);
        }else {
            return null;
        }
    }
}
