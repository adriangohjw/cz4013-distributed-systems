package databaseServices.caches;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import models.Facility;

public class FacilityCache extends Cache {

  public static int size = 5;

  public static HashMap<Integer, Facility> cache = new HashMap<Integer, Facility>();

  
  /** 
   * Get the total size of the cache
   * 
   * @return int  Return size of the cache
   */
  public static int getSize() {
    return size;
  }

  
  /** 
   * Retrieve cached objects based on name of a facility
   * 
   * @param name        Name of facility
   * @return Facility   Return object if cache found, else null
   */
  public static Facility get(String name) {
    for (HashMap.Entry<Integer, Facility> entry : cache.entrySet()) {
      if (entry.getValue().name.trim().equalsIgnoreCase(name.trim())) {
        System.out.println("Gotten from cache~");
        return entry.getValue();
      }
    }
    return null;
  }

  
  /** 
   * Retrieve cached objects based on ID of a facility
   * 
   * @param facilityId    ID of facility
   * @return Facility     Return object if cache found, else null
   */
  public static Facility get(Integer facilityId) {
    return cache.get(facilityId);
  }

  
  /** 
   * Insert single object to be cached
   * 
   * @param facility    Record to be cached
   */
  public static void put(Facility facility) {
    Integer key = facility.id;

    if (cache.containsKey(key)){
      cache.replace(key, facility);
      return ;
    }

    if (cache.size() == getSize()) {
      evictRandomCacheEntry();
    }

    cache.put(key, facility);
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
