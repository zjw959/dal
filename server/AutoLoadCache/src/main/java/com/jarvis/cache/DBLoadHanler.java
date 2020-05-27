package com.jarvis.cache;

public interface DBLoadHanler {
    public default boolean isDBLoad(Object result) {
        return false;
    }
}
