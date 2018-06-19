package memcachedserver.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import lombok.NonNull;

/**
 * For handling input from one client via {@link Socket}.
 */
public class InputHandler {
  @NonNull private final BufferedReader bufferedReader;

  public InputHandler(@NonNull final InputStream inputStream) throws IOException {
    this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
  }
}
