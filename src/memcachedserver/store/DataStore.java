package memcachedserver.store;

public interface DataStore {
  void set();

  void add();

  void replace();

  void append();

  void prepend();

  void get();

  void delete();
}
