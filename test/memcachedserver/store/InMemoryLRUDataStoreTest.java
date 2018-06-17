package memcachedserver.store;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class InMemoryLRUDataStoreTest {

  private static final int CAPACITY = 3;

  private static final String KEY_1 = "key 1";

  private static final Data DATA_1 = Data.of(0, 0, new Byte[] {1});
  private static final Data DATA_2 = Data.of(0, 0, new Byte[] {2, 2});

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

  }

  @Test
  public void testPrepend() {

  }

  @Test
  public void testGet() {

  }

  @Test
  public void testDelete() {

  }
}
