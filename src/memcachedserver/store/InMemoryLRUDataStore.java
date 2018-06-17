package memcachedserver.store;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ObjectArrays;

import lombok.NonNull;

/**
 * An in-memory cache for storing {@link Data} with LRU eviction policy.
 * The implementation is thread-safe.
 */
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
    rwLock.writeLock().lock();

    try {
      keyToData.put(key, data);
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  @Override
  public boolean add(@NonNull final String key, @NonNull final Data data) {
    rwLock.writeLock().lock();

    try {
      if (keyToData.containsKey(key)) {
        return false;
      }

      keyToData.put(key, data);
      return true;

    } finally {
      rwLock.writeLock().unlock();
    }
  }

  @Override
  public boolean replace(@NonNull final String key, @NonNull final Data data) {
    rwLock.writeLock().lock();

    try {
      if (keyToData.containsKey(key)) {
        keyToData.put(key, data);
        return true;
      }

      return false;

    } finally {
      rwLock.writeLock().unlock();
    }
  }

  @Override
  public void append(@NonNull final String key, @NonNull final Byte[] data) {
    rwLock.writeLock().lock();

    try {
      keyToData.computeIfPresent(key, (k, v) -> {
        Byte[] appendedData = ObjectArrays.concat(v.data(), data, Byte.class);
        return Data.of(v.flags(), v.expireTime(), appendedData);
      });
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  @Override
  public void prepend(@NonNull final String key, @NonNull final Byte[] data) {
    rwLock.writeLock().lock();

    try {
      keyToData.computeIfPresent(key, (k, v) -> {
        Byte[] prependedData = ObjectArrays.concat(data, v.data(), Byte.class);
        return Data.of(v.flags(), v.expireTime(), prependedData);
      });
    } finally {
      rwLock.writeLock().unlock();
    }
  }

  @Override
  public Map<String, Data> get(@NonNull final List<String> keys) {
    rwLock.readLock().lock();

    try {
      ImmutableMap.Builder<String, Data> builder = new ImmutableMap.Builder<>();

      for (String key : keys) {
        Data data = keyToData.get(key);
        if (data != null) {
          builder.put(key, data);
        }
      }

      return builder.build();

    } finally {
      rwLock.readLock().unlock();
    }
  }

  @Override
  public void delete(@NonNull final String key) {
    rwLock.writeLock().lock();

    try {
      keyToData.remove(key);
    } finally {
      rwLock.writeLock().unlock();
    }
  }
}
