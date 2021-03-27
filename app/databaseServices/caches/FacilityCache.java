package databaseServices.caches;

import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import models.Facility;

public class FacilityCache extends Cache {

  public static int size = 5;

  public static HashMap<Integer, Facility> cache = new HashMap<Integer, Facility>();

  public static int getSize() {
    return size;
  }

  public static Facility get(String name) {
    for (HashMap.Entry<Integer, Facility> entry : cache.entrySet()) {
      if (entry.getValue().name.trim().equalsIgnoreCase(name.trim())) {
        System.out.println("Gotten from cache~");
        return entry.getValue();
      }
    }
    return null;
  }

  public static Facility get(Integer facilityId) {
    for (HashMap.Entry<Integer, Facility> entry : cache.entrySet()) {
      if (entry.getKey() == facilityId) {
        System.out.println("Gotten from cache~");
        return entry.getValue();
      }
    }
    return null;
  }

  public static void put(Facility facility) {
    if (cache.size() == getSize()) {
      evictRandomCacheEntry();
    }
    cache.put(facility.id, facility);
  }

  private static void evictRandomCacheEntry() {
    Set<Integer> cacheKeys = cache.keySet();
    Integer randomCacheKey = cacheKeys.stream().skip(new Random().nextInt(cacheKeys.size())).findFirst().orElse(null);
    cache.remove(randomCacheKey);
  }

}
