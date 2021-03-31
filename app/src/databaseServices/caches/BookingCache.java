package src.databaseServices.caches;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import src.models.Booking;

public class BookingCache extends Cache {

  public static int size = 2;

  // Integer is the facility_id
  // We are storing all bookings relating to one facility in one value
  public static HashMap<Integer, List<Booking>> cache = new HashMap<Integer, List<Booking>>();

  
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
   * @param facilityId        ID of facility
   * @return List<Booking>    Return list of objects if cache found, else null
   */
  public static List<Booking> get(Integer facilityId) {
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
   * @param booking Record to be cached
   */
  public static void put(Booking booking) {
    if (cache.containsKey(booking.facilityId)) {
      List<Booking> bookings = cache.get(booking.facilityId);
      bookings.add(booking);
      cache.replace(booking.facilityId, bookings);
    }
  }

  
  /** 
   * Insert objects to be cached
   * Evict record from cache if cache is full
   * Note: assume all records in availabilities have the same facility_id
   * 
   * @param bookings  List of records to be cached
   */
  public static void put(List<Booking> bookings) {
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
