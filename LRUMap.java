package udp_server;

import java.util.*;

public class LRUMap<K, V> {
  private Map<K, V> map;

  public LRUMap(int capacity) {
    map = Collections.synchronizedMap(new LinkedHashMap<K, V>(capacity, 0.75f, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
      }
    });
  }

  public String get(K key) {
	if()
    return Optional.ofNullable(map.get(key));
  }

  public void put(K key, V value) {
    map.put(key, value);
  }
}