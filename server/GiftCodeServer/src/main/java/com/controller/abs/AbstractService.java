package com.controller.abs;

public interface AbstractService<K, V> {

    /**
     * @param key
     * @return
     */
    V findByKey(K key);

    /**
     * @param key
     * @return
     */
    V remove(K key);

    /**
     * @param key
     * @return
     */
    boolean isExist(K key);

    /**
     * @param value
     */
    V save(V value);
}
