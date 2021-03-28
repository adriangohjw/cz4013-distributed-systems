package databaseServices.caches;

import java.util.Random;

public class Cache {

  public static int size;

  public static int getSize() {
    return size;
  }

  protected Integer generateCacheIndexToEvict() {
    return new Random().nextInt(getSize());
  };

}
