package memcachedserver.handler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import lombok.NonNull;

/**
 * For handling output to one client via {@link Socket}.
 */
public class OutputHandler {
  @NonNull private final DataOutputStream outputStream;

  public OutputHandler(@NonNull final Socket socket) throws IOException {
    this.outputStream = new DataOutputStream(socket.getOutputStream());
  }
}
