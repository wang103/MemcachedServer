package memcachedserver.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import lombok.RequiredArgsConstructor;

public class InMemoryLRUDataStoreTest {

  private static final int CAPACITY = 3;

  private static final String KEY_1 = "key 1";
  private static final String KEY_2 = "key 2";
  private static final String KEY_3 = "key 3";
  private static final String KEY_4 = "key 4";

  private static final Data DATA_1 = Data.of(0, 0, new Byte[] {1});
  private static final Data DATA_2 = Data.of(0, 0, new Byte[] {2, 2});
  private static final Data DATA_3 = Data.of(0, 0, new Byte[] {3, 3, 3});
  private static final Data DATA_4 = Data.of(0, 0, new Byte[] {4, 4, 4, 4});

  private InMemoryLRUDataStore store;

  @Before
  public void setUp() {
    store = new InMemoryLRUDataStore(CAPACITY);
  }

  @Test
  public void testSet() {
    store.set(KEY_1, DATA_1);
    assertEquals(DATA_1, store.get(ImmutableList.of(KEY_1)).get(KEY_1));

    store.set(KEY_1, DATA_2);
    assertEquals(DATA_2, store.get(ImmutableList.of(KEY_1)).get(KEY_1));
  }

  @Test
  public void testAdd() {
    assertTrue(store.add(KEY_1, DATA_1));
    assertFalse(store.add(KEY_1, DATA_2));
  }

  @Test
  public void testReplace() {
    assertFalse(store.replace(KEY_1, DATA_1));
    store.set(KEY_1, DATA_1);
    assertTrue(store.replace(KEY_1, DATA_2));
  }

  @Test
  public void testAppend() {
    Byte[] extraData = new Byte[] {100};

    assertFalse(store.append(KEY_1, extraData));

    store.set(KEY_1, DATA_1);
    assertTrue(store.append(KEY_1, extraData));

    Data expectedData = Data.of(0, 0, new Byte[] {1, 100});
    assertEquals(expectedData, store.get(ImmutableList.of(KEY_1)).get(KEY_1));
  }

  @Test
  public void testPrepend() {
    Byte[] extraData = new Byte[] {100};

    assertFalse(store.prepend(KEY_1, extraData));

    store.set(KEY_1, DATA_1);
    assertTrue(store.prepend(KEY_1, extraData));

    Data expectedData = Data.of(0, 0, new Byte[] {100, 1});
    assertEquals(expectedData, store.get(ImmutableList.of(KEY_1)).get(KEY_1));
  }

  @Test
  public void testGet() {
    assertEquals(ImmutableMap.of(), store.get(ImmutableList.of(KEY_1, KEY_2)));

    store.set(KEY_1, DATA_1);
    assertEquals(ImmutableMap.of(KEY_1, DATA_1), store.get(ImmutableList.of(KEY_1, KEY_2)));

    store.set(KEY_2, DATA_2);
    assertEquals(ImmutableMap.of(KEY_1, DATA_1, KEY_2, DATA_2), store.get(ImmutableList.of(KEY_1, KEY_2)));
  }

  @Test
  public void testDelete() {
    assertFalse(store.delete(KEY_1));

    store.set(KEY_1, DATA_1);

    assertTrue(store.delete(KEY_1));
    assertEquals(ImmutableMap.of(), store.get(ImmutableList.of(KEY_1)));
  }

  @Test
  public void testLRUEviction() {
    store.set(KEY_1, DATA_1);
    store.set(KEY_2, DATA_2);
    store.set(KEY_3, DATA_3);
    store.set(KEY_4, DATA_4);
    assertEquals(ImmutableMap.of(), store.get(ImmutableList.of(KEY_1)));

    store.get(ImmutableList.of(KEY_2));
    store.set(KEY_1, DATA_1);
    assertEquals(ImmutableMap.of(), store.get(ImmutableList.of(KEY_3)));
  }

  @Test
  public void testConcurrency() throws Exception {
    List<StoreWriter> writers = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      writers.add(new StoreWriter(store));
    }

    List<StoreReader> readers = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      readers.add(new StoreReader(store));
    }

    writers.forEach(StoreWriter::start);
    readers.forEach(StoreReader::start);

    for (StoreWriter writer : writers) {
      writer.join(10000);
    }

    for (StoreReader reader : readers) {
      reader.join(10000);
    }
  }

  @RequiredArgsConstructor
  private static class StoreWriter extends Thread {
    private final InMemoryLRUDataStore dataStore;

    @Override
    public void run() {
      for (int i = 0; i < 30; i++) {
        dataStore.set(KEY_1, DATA_1);

        dataStore.add(KEY_2, DATA_2);

        dataStore.replace(KEY_1, DATA_3);

        dataStore.append(KEY_3, new Byte[] {100});

        dataStore.prepend(KEY_4, new Byte[] {100});

        dataStore.delete(KEY_2);
      }
    }
  }

  @RequiredArgsConstructor
  private static class StoreReader extends Thread {
    private final InMemoryLRUDataStore dataStore;

    @Override
    public void run() {
      for (int i = 0; i < 100; i++) {
        dataStore.get(ImmutableList.of(KEY_1, KEY_2, KEY_3, KEY_4));
      }
    }
  }
}
