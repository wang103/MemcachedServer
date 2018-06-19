package memcachedserver.handler;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import lombok.NonNull;

/**
 * For handling output to one client via {@link Socket}.
 */
public class OutputHandler {
  @NonNull private final DataOutputStream output;

  public OutputHandler(@NonNull final OutputStream outputStream) throws IOException {
    this.output = new DataOutputStream(outputStream);
  }
}
