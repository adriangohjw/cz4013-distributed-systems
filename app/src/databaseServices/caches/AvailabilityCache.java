package src.databaseServices.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import src.models.Availability;

public class AvailabilityCache {

  public static int size = 2;

  // Integer is the facility_id
  // We are storing all availabilities relating to one facility in one value
  public static HashMap<Integer, List<Availability>> cache = new HashMap<Integer, List<Availability>>();

  
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
   * @param facilityId            ID of facility
   * @return List<Availability>   Return list of objects if cache found, else null
   */
  public static List<Availability> get(Integer facilityId) {
    if (cache.containsKey(facilityId)) {
      System.out.println("Gotten from cache~");
      return cache.get(facilityId);
    }
    return null;
  }

  
  /** 
   * Insert objects to be cached
   * Evict record from cache if cache is full
   * Note: assume all records in availabilities have the same facility_id
   * 
   * @param availabilities  List of records to be cached
   */
  public static void put(List<Availability> availabilities) {
    Integer key = (availabilities.size() == 0) ? null : availabilities.get(0).facilityId;

    if (cache.containsKey(key)){
      cache.replace(key, availabilities);
      return ;
    }
    
    if (cache.size() == getSize()) {
      evictRandomCacheEntry();
    }

    cache.put(key, availabilities);
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
