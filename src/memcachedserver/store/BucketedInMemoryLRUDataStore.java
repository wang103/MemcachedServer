package memcachedserver.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An in-memory bucketed cache for storing {@link Data}.
 * The LRU eviction policy applies within each bucket.
 * The implementation is thread-safe.
 */
public class BucketedInMemoryLRUDataStore implements DataStore {

  private final int numBuckets;
  private final List<InMemoryLRUDataStore> dataStores;

  public BucketedInMemoryLRUDataStore(final int numBuckets, final int bucketCapacity) {
    this.numBuckets = numBuckets;

    this.dataStores = new ArrayList<>();
    for (int i = 0; i < numBuckets; i++) {
      dataStores.add(new InMemoryLRUDataStore(bucketCapacity));
    }
  }

  @Override
  public void set(String key, Data data) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean add(String key, Data data) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean replace(String key, Data data) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean append(String key, Byte[] data) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean prepend(String key, Byte[] data) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Map<String, Data> get(List<String> keys) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean delete(String key) {
    // TODO Auto-generated method stub
    return false;
  }
}
