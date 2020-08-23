package com.darryl.jwt.annotation.cloud.utils;

import com.google.common.collect.Maps;

import java.util.concurrent.ConcurrentMap;

/**
 * @Auther: Darryl
 * @Description: 自定义map
 * @Date: 2020/08/23
 */
public abstract class LazyMap<K, V> {

	private ConcurrentMap<K, V> map = Maps.newConcurrentMap();

	public V get (K key) {
		V value = map.get(key);
		if (value != null) {
			return value;
		}
		value = load(key);
		V existsValue = (value == null ? null : map.putIfAbsent(key, value));
		return existsValue;
	}

	protected abstract V load(K key);

}
