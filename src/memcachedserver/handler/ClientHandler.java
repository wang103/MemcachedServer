package memcachedserver.handler;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import lombok.NonNull;
import memcachedserver.command.Command;
import memcachedserver.store.DataStore;

/**
 * For handling communication with one client via {@link Socket}.
 * This class is responsible for closing the socket when it's no longer needed.
 */
public class ClientHandler implements Runnable {
  @NonNull private final Socket socket;
  @NonNull private final DataStore dataStore;
  @NonNull private final InputHandler inputHandler;
  @NonNull private final OutputHandler outputHandler;

  public ClientHandler(@NonNull final Socket socket, @NonNull final DataStore dataStore) throws IOException {
    this.socket = socket;
    this.dataStore = dataStore;
    this.inputHandler = new InputHandler(socket.getInputStream());
    this.outputHandler = new OutputHandler(socket.getOutputStream());
  }

  @Override
  public void run() {
    try {
      while (true) {
        Optional<Command> command = inputHandler.readCommand();

        if (!command.isPresent()) {
          outputHandler.writeLine("ERROR");
          continue;
        }

        handleCommand(command.get());
      }
    } catch (IOException e) {
      // ignore IOException, client closed its connection, so we just need to
      // release our resources and exit this thread
    } finally {
      releaseResources();
    }
  }

  private void handleCommand(final Command command) {

  }

  private void releaseResources() {
    try {
      inputHandler.close();
      outputHandler.close();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
