package com.sst.nt.lms.orch.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A helper class to make creation of a map less verbose.
 * @author Jonathan Lovelace
 */
public final class MapBuilder<K, V> {
	/**
	 * State in progress.
	 */
	private final Map<K, V> map;
	/**
	 * Constructor.
	 */
	public MapBuilder() {
		map = new HashMap<>();
	}

	/**
	 * Add a new key-value pair to the map (overwriting any previous value for that
	 * key, if any).
	 *
	 * @param key   the key to add to the map
	 * @param value the value to associate with that key
	 */
	public MapBuilder<K, V> entry(final K key, final V value) {
		map.put(key, value);
		return this;
	}
	/**
	 * Get the map we've been building.
	 * @return an immutable view of the map so far.
	 */
	public Map<K, V> build() {
		return Collections.unmodifiableMap(map);
	}
}
