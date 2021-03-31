package server;

import java.util.*;

public class LRUMap<K, V> {
	
  private Map<K, V> map;

  public LRUMap(int max) {
    map = Collections.synchronizedMap(new LinkedHashMap<K, V>(max, 0.75f, true) {
      @Override
      protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > max;
      }
    });
  }

  
  /** 
   * @param key
   * @return Optional<V>
   */
  public Optional<V> get(K key) {
    return Optional.ofNullable(map.get(key));
  }

  
  /** 
   * @param key
   * @param value
   */
  public void put(K key, V value) {
    map.put(key, value);
  }
}