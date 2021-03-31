package databaseServices.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import models.Booking;

public class BookingCache extends Cache {

  public static int size = 2;

  // Integer is the facility_id
  // We are storing all bookings relating to one facility in one value
  public static HashMap<Integer, List<Booking>> cache = new HashMap<Integer, List<Booking>>();

  
  /** 
   * @return int
   */
  public static int getSize() {
    return size;
  }

  
  /** 
   * @param facilityId
   * @return List<Booking>
   */
  public static List<Booking> get(Integer facilityId) {
    if (cache.containsKey(facilityId)) {
      System.out.println("Gotten from cache~");
      return cache.get(facilityId);
    }
    return null;
  }

  
  /** 
   * @param booking
   */
  // for single entry, only add to cache if it's facility_id already exist in cache (for simplicity)
  public static void put(Booking booking) {
    if (cache.containsKey(booking.facilityId)) {
      List<Booking> bookings = cache.get(booking.facilityId);
      bookings.add(booking);
      cache.replace(booking.facilityId, bookings);
    }
  }

  
  /** 
   * @param bookings
   */
  public static void put(List<Booking> bookings) {
    // assume all records in availabilities have the same facility_id
    Integer key = (bookings.size() == 0) ? null : bookings.get(0).facilityId;

    if (cache.containsKey(key)){
      cache.replace(key, bookings);
      return ;
    }
    
    if (cache.size() == getSize()) {
      evictRandomCacheEntry();
    }

    cache.put(key, bookings);
  }

  private static void evictRandomCacheEntry() {
    Set<Integer> cacheKeys = cache.keySet();
    Integer randomCacheKey = cacheKeys.stream().skip(new Random().nextInt(cacheKeys.size())).findFirst().orElse(null);
    cache.remove(randomCacheKey);
  }
  
}
