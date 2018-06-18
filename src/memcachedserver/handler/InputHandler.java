package memcachedserver.handler;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import lombok.NonNull;

/**
 * For handling input from one client via {@link Socket}.
 */
public class InputHandler {
  @NonNull private final DataInputStream inputStream;

  public InputHandler(@NonNull final Socket socket) throws IOException {
    this.inputStream = new DataInputStream(socket.getInputStream());
  }
}
