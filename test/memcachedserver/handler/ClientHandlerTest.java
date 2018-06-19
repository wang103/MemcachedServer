package memcachedserver.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import memcachedserver.command.DeleteCommand;
import memcachedserver.command.RetrievalCommand;
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
