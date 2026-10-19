package com.perfect.client.core.utils.collection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class ListRemoveHashMap<K, V> extends ConcurrentHashMap<K, V> {

	public void removeAll(List<K> keysToRemove) {
		for (K keyToRemove : keysToRemove) {
			this.remove(keyToRemove);
		}
	}
}
