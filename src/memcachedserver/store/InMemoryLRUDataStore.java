package memcachedserver.store;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InMemoryLRUDataStore implements DataStore {

  private final long capacity;

  @Override
  public void set(String key, Data data) {
    // TODO Auto-generated method stub

  }

  @Override
  public void add(String key, Data data) {
    // TODO Auto-generated method stub

  }

  @Override
  public void replace(String key, Data data) {
    // TODO Auto-generated method stub

  }

  @Override
  public void append(String key, byte[] data) {
    // TODO Auto-generated method stub

  }

  @Override
  public void prepend(String key, byte[] data) {
    // TODO Auto-generated method stub

  }

  @Override
  public Map<String, Data> get(List<String> keys) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(String key) {
    // TODO Auto-generated method stub

  }
}
