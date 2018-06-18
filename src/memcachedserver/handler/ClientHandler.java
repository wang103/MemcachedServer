package memcachedserver.handler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lombok.NonNull;
import memcachedserver.store.DataStore;

/**
 * For handling communication with one client via {@link Socket}.
 */
public class ClientHandler implements Runnable {
  @NonNull private final Socket socket;
  @NonNull private final DataInputStream inputStream;
  @NonNull private final DataOutputStream outputStream;

  @NonNull private final DataStore dataStore;

  public ClientHandler(@NonNull final Socket socket, @NonNull final DataStore dataStore) throws IOException {
    this.socket = socket;

    this.inputStream = new DataInputStream(socket.getInputStream());
    this.outputStream = new DataOutputStream(socket.getOutputStream());

    this.dataStore = dataStore;
  }

  @Override
  public void run() {

  }
}
