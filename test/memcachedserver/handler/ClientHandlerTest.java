package memcachedserver.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;

import memcachedserver.command.DeleteCommand;
import memcachedserver.store.DataStore;

public class ClientHandlerTest {
  private final Socket socket = mock(Socket.class);
  private final DataStore dataStore = mock(DataStore.class);
  private final InputHandler inputHandler = mock(InputHandler.class);
  private final OutputHandler outputHandler = mock(OutputHandler.class);

  private ClientHandler clientHandler;

  @Before
  public void setUp() throws IOException {
    clientHandler = new ClientHandler(socket, dataStore, inputHandler, outputHandler);
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
