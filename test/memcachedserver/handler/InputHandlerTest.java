package memcachedserver.handler;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import memcachedserver.command.DeleteCommand;
import memcachedserver.command.RetrievalCommand;
import memcachedserver.command.StorageCommand;

public class InputHandlerTest {
  private static final String KEY_1 = "key1";
  private static final String KEY_2 = "key2";
  private static final String KEY_3 = "key3";

  private InputHandler inputHandler;

  @Before
  public void setUp() throws IOException {
    InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

    inputHandler = new InputHandler(inputStream);
  }

  @Test
  public void testToStorageCommand() {
    String[] components = {"prepend", KEY_1, "1", "2", "3"};
    assertEquals(
        Optional.of(StorageCommand.of("prepend", KEY_1, 1, 2, 3)),
        inputHandler.toStorageCommand(components));

    String[] invalidComponents = {"add", "KEY_2"};
    assertEquals(Optional.empty(), inputHandler.toStorageCommand(invalidComponents));
  }

  @Test
  public void testToRetrievalCommand() {
    String[] components = {"get", KEY_1, KEY_2, KEY_3};
    assertEquals(
        Optional.of(RetrievalCommand.of("get", ImmutableList.of(KEY_1, KEY_2, KEY_3))),
        inputHandler.toRetrievalCommand(components));

    String[] invalidComponents = {"get"};
    assertEquals(Optional.empty(), inputHandler.toRetrievalCommand(invalidComponents));
  }

  @Test
  public void testToDeleteCommand() {
    String[] components = {"delete", KEY_1};
    assertEquals(Optional.of(DeleteCommand.of("delete", KEY_1)), inputHandler.toDeleteCommand(components));

    String[] invalidComponents = {"delete", KEY_1, KEY_2};
    assertEquals(Optional.empty(), inputHandler.toDeleteCommand(invalidComponents));
  }
}
