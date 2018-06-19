package memcachedserver.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import memcachedserver.command.DeleteCommand;
import memcachedserver.command.RetrievalCommand;
import memcachedserver.command.StorageCommand;
import memcachedserver.store.Data;
import memcachedserver.store.DataStore;

public class ClientHandlerTest {
  private static final String KEY_1 = "key1";
  private static final String KEY_2 = "key2";
  private static final String KEY_3 = "key3";

  private static final Data DATA_1 = Data.of(1, 1, new Byte[] {});
  private static final Data DATA_3 = Data.of(3, 3, new Byte[] {});

  private final Socket socket = mock(Socket.class);
  private final DataStore dataStore = mock(DataStore.class);
  private final InputHandler inputHandler = mock(InputHandler.class);
  private final OutputHandler outputHandler = mock(OutputHandler.class);

  private ClientHandler clientHandler = new ClientHandler(socket, dataStore, inputHandler, outputHandler);

  @Before
  public void setUp() throws IOException {
    when(dataStore.get(ImmutableList.of(KEY_1, KEY_2, KEY_3))).thenReturn(
        ImmutableMap.of(KEY_1, DATA_1, KEY_3, DATA_3));

    when(dataStore.add(KEY_1, DATA_1)).thenReturn(true);

    when(inputHandler.readData(0)).thenReturn(Optional.of(new Byte[] {}));
  }

  @Test
  public void testHandleStorageCommand() throws IOException {
    StorageCommand command = StorageCommand.of("add", KEY_1, 1, 1, 0);

    clientHandler.handleStorageCommand(command);
    verify(dataStore).add(KEY_1, DATA_1);
    verify(outputHandler).writeLine("STORED");
  }

  @Test
  public void testHandleStorageCommandKeyTooLong() throws IOException {
    String key = StringUtils.repeat('a', 251);
    StorageCommand command = StorageCommand.of("add", key, 1, 1, 0);

    clientHandler.handleStorageCommand(command);
    verifyZeroInteractions(dataStore);
    verifyZeroInteractions(inputHandler);

    verify(outputHandler).writeLine("CLIENT_ERROR bad command line format");
  }

  @Test
  public void testHandleStorageCommandKeyHasControlChars() throws IOException {
    String key = new String(new byte[] {'h', 0x07, 'i'}, StandardCharsets.UTF_8);
    StorageCommand command = StorageCommand.of("add", key, 1, 1, 0);

    clientHandler.handleStorageCommand(command);
    verifyZeroInteractions(dataStore);
    verifyZeroInteractions(inputHandler);

    verify(outputHandler).writeLine("CLIENT_ERROR bad command line format");
  }

  @Test
  public void testHandleStorageCommandNotStored() throws IOException {
    StorageCommand command = StorageCommand.of("add", KEY_1, 1, 1, 0);

    when(dataStore.add(KEY_1, DATA_1)).thenReturn(false);
    clientHandler.handleStorageCommand(command);
    verify(outputHandler).writeLine("NOT_STORED");
  }

  @Test
  public void testHandleStorageCommandBadData() throws IOException {
    StorageCommand command = StorageCommand.of("add", KEY_1, 1, 1, 0);

    when(inputHandler.readData(0)).thenReturn(Optional.empty());
    clientHandler.handleStorageCommand(command);
    verifyZeroInteractions(dataStore);
    verify(outputHandler).writeLine("CLIENT_ERROR bad data chunk");
  }

  @Test
  public void testHandleRetrievalCommand() throws IOException {
    RetrievalCommand command = RetrievalCommand.of("get", ImmutableList.of(KEY_1, KEY_2, KEY_3));

    clientHandler.handleRetrievalCommand(command);
    verify(outputHandler).writeData(KEY_1, DATA_1);
    verify(outputHandler).writeData(KEY_3, DATA_3);
    verify(outputHandler).writeLine("END");
  }

  @Test
  public void testHandleDeleteCommand() throws IOException {
    DeleteCommand command = DeleteCommand.of("delete", "key");

    when(dataStore.delete("key")).thenReturn(true);
    clientHandler.handleDeleteCommand(command);
    verify(outputHandler).writeLine("DELETED");

    when(dataStore.delete("key")).thenReturn(false);
    clientHandler.handleDeleteCommand(command);
    verify(outputHandler).writeLine("NOT_FOUND");
  }
}
