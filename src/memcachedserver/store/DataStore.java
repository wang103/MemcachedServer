package memcachedserver.store;

import java.util.List;
import java.util.Map;

/**
 * Interface for a {@link Data} store.
 */
public interface DataStore {

  /**
   * Set data for the given key.
   *
   * @param key The key
   * @param data The {@code Data}
   */
  void set(String key, Data data);

  /**
   * Set data for the given key only if the key does not exist
   *
   * @param key The key
   * @param data The {@code Data}
   * @return true if succeeded, otherwise false
   */
  boolean add(String key, Data data);

  /**
   * Set data for the given key only if the key already exists
   *
   * @param key The key
   * @param data The {@code Data}
   * @return true if succeeded, otherwise false
   */
  boolean replace(String key, Data data);

  /**
   * Add the given data to an existing key after existing {@code Data}
   *
   * @param key They key
   * @param data The data
   */
  void append(String key, Byte[] data);

  /**
   * Add the given data to an existing key before existing {@code Data}
   *
   * @param key The key
   * @param data The data
   */
  void prepend(String key, Byte[] data);

  /**
   * Retrieve {@code Data} corresponding to the given set of keys
   *
   * @param keys The list of keys
   * @return A map from key to {@code Data}
   */
  Map<String, Data> get(List<String> keys);

  /**
   * Delete the given key
   *
   * @param key The key
   */
  void delete(String key);
}
