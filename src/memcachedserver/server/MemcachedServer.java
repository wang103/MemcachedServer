package memcachedserver.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import memcachedserver.handler.ClientHandler;
import memcachedserver.store.DataStore;

/**
 * This class represents one instance of Memcached server.
 */
@RequiredArgsConstructor
public class MemcachedServer {
  @NonNull private final DataStore dataStore;

  /**
   * Start the server.
   *
   * @param port The TCP port the server listens on
   * @throws IOException if an I/O error occurs when creating the {@code ServerSocket}
   */
  public void start(final int port) throws IOException {
    ServerSocket ss = new ServerSocket(port);

    try {
      while (true) {
        Socket socket = ss.accept();

        System.out.println("A new client is connected");

        Runnable clientHandler = new ClientHandler(socket, dataStore);

        new Thread(clientHandler).start();
      }
    } catch (IOException e) {
      // unable to accept incoming clients, terminate the server
      e.printStackTrace();
    } finally {
      ss.close();
    }
  }
}
