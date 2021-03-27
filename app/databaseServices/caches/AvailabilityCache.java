package databaseServices.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import models.Availability;

public class AvailabilityCache {

  public static int size = 2;

  // Integer is the facility_id
  // We are storing all availabilities relating to one facility in one value
  public static HashMap<Integer, List<Availability>> cache = new HashMap<Integer, List<Availability>>();

  public static int getSize() {
    return size;
  }

  public static List<Availability> get(Integer facilityId) {
    if (cache.containsKey(facilityId)) {
      System.out.println("Gotten from cache~");
      return cache.get(facilityId);
    }
    return null;
  }

  public static void put(List<Availability> availabilities) {
    // assume all records in availabilities have the same facility_id
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

  private static void evictRandomCacheEntry() {
    Set<Integer> cacheKeys = cache.keySet();
    Integer randomCacheKey = cacheKeys.stream().skip(new Random().nextInt(cacheKeys.size())).findFirst().orElse(null);
    cache.remove(randomCacheKey);
  }
  
}
