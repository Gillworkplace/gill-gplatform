package com.gill.common.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Pair
 *
 * @author gill
 * @version 2024/01/23
 **/
@Getter
@EqualsAndHashCode
public class Pair<K, V> {

    private final K key;

    private final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * of
     *
     * @param key   key
     * @param value value
     * @param <K>   k
     * @param <V>   v
     * @return pair
     */
    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    @Override
    public String toString() {
        return key.toString() + "=" + value.toString();
    }
}
