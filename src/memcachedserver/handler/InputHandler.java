package memcachedserver.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import lombok.NonNull;
import memcachedserver.command.Command;
import memcachedserver.command.CommandType;

/**
 * For handling input from one client via {@link Socket}.
 */
public class InputHandler {
  @NonNull private final BufferedReader bufferedReader;

  public InputHandler(@NonNull final InputStream inputStream) throws IOException {
    this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
  }

  /**
   * Read a line and get the corresponding {@code Command}
   *
   * @return the {@code Command}
   * @throws IOException if an I/O error occurs
   */
  public Optional<Command> readCommand() throws IOException {
    String line = bufferedReader.readLine().trim();
    String[] components = line.split("\\s+");
    return toCommand(components);
  }

  private Optional<Command> toCommand(final String[] components) {
    int numComponents = components.length;
    if (numComponents < 1) {
      return Optional.empty();
    }

    String commandName = components[0];
    Optional<CommandType> commandType = Command.toCommandType(commandName);
    if (!commandType.isPresent()) {
      return Optional.empty();
    }

    return null;
  }
}
