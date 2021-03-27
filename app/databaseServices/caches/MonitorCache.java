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

  public static int getSize() {
    return size;
  }

  public static List<Monitor> get(Integer facilityId) {
    if (cache.containsKey(facilityId)) {
      System.out.println("Gotten from cache~");
      return cache.get(facilityId);
    }
    return null;
  }

  public static void put(Monitor monitor) {
    List<Monitor> monitors;

    if (cache.containsKey(monitor.facilityId)) {
      monitors = cache.get(monitor.facilityId);
    }
    else {
      monitors = new ArrayList<Monitor>();

      if (cache.size() == getSize()) {
        evictRandomCacheEntry();
      }
    }

    monitors.add(monitor);

    cache.put(monitor.facilityId, monitors);
  }

  public static void put(List<Monitor> monitors) {
    if (cache.size() == getSize()) {
      evictRandomCacheEntry();
    }

    // assume all records in monitors have the same facility_id
    Integer key = (monitors.size() == 0) ? null : monitors.get(0).facilityId;
    cache.put(key, monitors);
  }

  private static void evictRandomCacheEntry() {
    Set<Integer> cacheKeys = cache.keySet();
    Integer randomCacheKey = cacheKeys.stream().skip(new Random().nextInt(cacheKeys.size())).findFirst().orElse(null);
    cache.remove(randomCacheKey);
  }
  
}
