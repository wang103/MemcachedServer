package memcachedserver.store;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.NonNull;

public class InMemoryLRUDataStore implements DataStore {

  private static final boolean DEFAULT_LOCK_IS_FAIR = false;  // for higher throughput
  private static final float DEFAULT_LOAD_FACTOR = 0.75f;

  private final ReadWriteLock rwLock = new ReentrantReadWriteLock(DEFAULT_LOCK_IS_FAIR);
  private final LinkedHashMap<String, Data> keyToData;

  public InMemoryLRUDataStore(final int capacity) {
    this.keyToData = new LinkedHashMap<String, Data>(capacity, DEFAULT_LOAD_FACTOR, true) {
      private static final long serialVersionUID = -5250528325313906121L;

      @Override
      protected boolean removeEldestEntry(Map.Entry<String, Data> eldest) {
        return size() > capacity;
      }
    };
  }

  @Override
  public void set(@NonNull final String key, @NonNull final Data data) {
    // TODO Auto-generated method stub

  }

  @Override
  public void add(@NonNull final String key, @NonNull final Data data) {
    // TODO Auto-generated method stub

  }

  @Override
  public void replace(@NonNull final String key, @NonNull final Data data) {
    // TODO Auto-generated method stub

  }

  @Override
  public void append(@NonNull final String key, @NonNull final byte[] data) {
    // TODO Auto-generated method stub

  }

  @Override
  public void prepend(@NonNull final String key, @NonNull final byte[] data) {
    // TODO Auto-generated method stub

  }

  @Override
  public Map<String, Data> get(@NonNull final List<String> keys) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(@NonNull final String key) {
    // TODO Auto-generated method stub

  }
}
