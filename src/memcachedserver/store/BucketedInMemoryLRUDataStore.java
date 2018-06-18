package memcachedserver.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.NonNull;

/**
 * An in-memory bucketed cache for storing {@link Data}.
 * The LRU eviction policy applies within each bucket.
 * The implementation is thread-safe.
 */
public class BucketedInMemoryLRUDataStore implements DataStore {

  private final int numBuckets;
  @NonNull private final List<InMemoryLRUDataStore> dataStores;

  public BucketedInMemoryLRUDataStore(final int numBuckets, final int bucketCapacity) {
    this.numBuckets = numBuckets;

    this.dataStores = new ArrayList<>();
    for (int i = 0; i < numBuckets; i++) {
      dataStores.add(new InMemoryLRUDataStore(bucketCapacity));
    }
  }

  @Override
  public void set(@NonNull final String key, @NonNull final Data data) {
    getBucketDataStore(key).set(key, data);
  }

  @Override
  public boolean add(@NonNull final String key, @NonNull final Data data) {
    return getBucketDataStore(key).add(key, data);
  }

  @Override
  public boolean replace(@NonNull final String key, @NonNull final Data data) {
    return getBucketDataStore(key).replace(key, data);
  }

  @Override
  public boolean append(@NonNull final String key, @NonNull final Byte[] data) {
    return getBucketDataStore(key).append(key, data);
  }

  @Override
  public boolean prepend(@NonNull final String key, @NonNull final Byte[] data) {
    return getBucketDataStore(key).prepend(key, data);
  }

  @Override
  public Map<String, Data> get(@NonNull final List<String> keys) {
    Map<InMemoryLRUDataStore, List<String>> bucketDataStoreToKeys =
        keys.stream().collect(Collectors.groupingBy(this::getBucketDataStore));

    Map<String, Data> results = new HashMap<>();

    for (Map.Entry<InMemoryLRUDataStore, List<String>> entry : bucketDataStoreToKeys.entrySet()) {
      InMemoryLRUDataStore bucketDataStore = entry.getKey();
      List<String> bucketKeys = entry.getValue();

      results.putAll(bucketDataStore.get(bucketKeys));
    }

    return results;
  }

  @Override
  public boolean delete(@NonNull final String key) {
    return getBucketDataStore(key).delete(key);
  }

  private InMemoryLRUDataStore getBucketDataStore(final String key) {
    int index = Math.floorMod(key.hashCode(), numBuckets);
    return dataStores.get(index);
  }
}
