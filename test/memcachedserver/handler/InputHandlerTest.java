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
  private static final String INPUT = "  replace  key 8   9 10    ";

  private static final String KEY_1 = "key1";
  private static final String KEY_2 = "key2";
  private static final String KEY_3 = "key3";

  private InputHandler inputHandler;

  @Before
  public void setUp() throws IOException {
    InputStream inputStream = new ByteArrayInputStream(
        INPUT.getBytes(StandardCharsets.UTF_8));

    inputHandler = new InputHandler(inputStream);
  }

  @Test
  public void testReadCommand() throws IOException {
    assertEquals(
        Optional.of(StorageCommand.of("replace", "key", 8, 9, 10)),
        inputHandler.readCommand());
  }

  @Test
  public void testToCommand() {
    String[] storageCommand = {"append", KEY_1, "0", "1", "2"};
    assertEquals(
        Optional.of(StorageCommand.of("append", KEY_1, 0, 1, 2)),
        inputHandler.toCommand(storageCommand));

    String[] retrievalCommand = {"get", KEY_1, KEY_2};
    assertEquals(
        Optional.of(RetrievalCommand.of("get", ImmutableList.of(KEY_1, KEY_2))),
        inputHandler.toCommand(retrievalCommand));

    String[] deleteCommand = {"delete", KEY_3};
    assertEquals(Optional.of(DeleteCommand.of("delete", KEY_3)), inputHandler.toCommand(deleteCommand));
  }

  @Test
  public void testToCommandFailure() {
    String[] invalidComponents1 = {};
    assertEquals(Optional.empty(), inputHandler.toCommand(invalidComponents1));

    String[] invalidComponents2 = {"garbage", KEY_1};
    assertEquals(Optional.empty(), inputHandler.toCommand(invalidComponents2));
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
