package com.bniproductions.android.myanimationmaker;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, Bitmap>{
    public static int getDefaultLruCacheSize() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return cacheSize;
    }

    /*
     * defaults using 1/8th of the heap for the cache
     */
    public BitmapLruCache() {
        this(getDefaultLruCacheSize());
    }

    /*
     * allows cache size to be set by parent
     */
    public BitmapLruCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.util.LruCache#sizeOf(java.lang.Object, java.lang.Object)
     * returns the size of the bitmap at the key
     */
    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
    }

    public Bitmap getBitmapFromMemoryCache(String key) {
        return get(key);
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        put(key, bitmap);
    }
}
