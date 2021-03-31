package databaseServices.caches;

import java.util.Random;

public class Cache {

  public static int size;

  
  /** 
   * Get the total size of the cache
   * 
   * @return int  Return size of the cache
   */
  public static int getSize() {
    return size;
  }

  
  /** 
   * Generate a randomized cache index to evict
   * 
   * @return Integer  Return index of cache to evict
   */
  protected Integer generateCacheIndexToEvict() {
    return new Random().nextInt(getSize());
  };

}
