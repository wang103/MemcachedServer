package memcachedserver.handler;

import java.io.IOException;
import java.net.Socket;

import lombok.NonNull;
import memcachedserver.store.DataStore;

/**
 * For handling communication with one client via {@link Socket}.
 */
public class ClientHandler implements Runnable {
  @NonNull private final Socket socket;
  @NonNull private final DataStore dataStore;
  @NonNull private final InputHandler inputHandler;
  @NonNull private final OutputHandler outputHandler;

  public ClientHandler(@NonNull final Socket socket, @NonNull final DataStore dataStore) throws IOException {
    this.socket = socket;
    this.dataStore = dataStore;
    this.inputHandler = new InputHandler(socket);
    this.outputHandler = new OutputHandler(socket);
  }

  @Override
  public void run() {

  }
}
