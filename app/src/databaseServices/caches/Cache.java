package databaseServices.caches;

import java.util.Random;

public class Cache {

  public static int size;

  
  /** 
   * @return int
   */
  public static int getSize() {
    return size;
  }

  
  /** 
   * @return Integer
   */
  protected Integer generateCacheIndexToEvict() {
    return new Random().nextInt(getSize());
  };

}
