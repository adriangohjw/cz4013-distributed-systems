package databaseServices.caches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import models.Monitor;

public class MonitorCache extends Cache {

  public static int size = 2;

  // Integer is the facility_id
  // We are storing all monitors relating to one facility in one value
  public static HashMap<Integer, List<Monitor>> cache = new HashMap<Integer, List<Monitor>>();

  
  /** 
   * Get the total size of the cache
   * 
   * @return int  Return size of the cache
   */
  public static int getSize() {
    return size;
  }

  
  /** 
   * Retrieve cached objects based on ID of a facility
   * 
   * @param facilityId      ID of facility
   * @return List<Monitor>  Return list of objects if cache found, else null
   */
  public static List<Monitor> get(Integer facilityId) {
    if (cache.containsKey(facilityId)) {
      System.out.println("Gotten from cache~");
      return cache.get(facilityId);
    }
    return null;
  }

  
  /** 
   * Insert single object to be cached
   * Note: only add to cache if it's facility_id already exist in cache (for simplicity)
   * 
   * @param monitor   Record to be cached
   */
  // for single entry, only add to cache if it's facility_id already exist in cache (for simplicity)
  public static void put(Monitor monitor) {
    if (cache.containsKey(monitor.facilityId)) {
      List<Monitor> monitors = cache.get(monitor.facilityId);
      monitors.add(monitor);
      cache.replace(monitor.facilityId, monitors);
    }
  }

  
  /** 
   * Insert objects to be cached
   * Evict record from cache if cache is full
   * Note: assume all records in availabilities have the same facility_id
   * 
   * @param monitors  List of records to be cached
   */
  public static void put(List<Monitor> monitors) {
    // assume all records in monitors have the same facility_id
    Integer key = (monitors.size() == 0) ? null : monitors.get(0).facilityId;

    if (cache.containsKey(key)){
      cache.replace(key, monitors);
      return ;
    }
    
    if (cache.size() == getSize()) {
      evictRandomCacheEntry();
    }
    
    cache.put(key, monitors);
  }

  /** 
   * Generate a randomized cache index to evict
   * 
   * @return Integer  Return index of cache to evict
   */
  private static void evictRandomCacheEntry() {
    Set<Integer> cacheKeys = cache.keySet();
    Integer randomCacheKey = cacheKeys.stream().skip(new Random().nextInt(cacheKeys.size())).findFirst().orElse(null);
    cache.remove(randomCacheKey);
  }
  
}
