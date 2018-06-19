package memcachedserver.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;

import lombok.NonNull;
import memcachedserver.command.Command;
import memcachedserver.command.CommandType;
import memcachedserver.command.DeleteCommand;
import memcachedserver.command.RetrievalCommand;

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
    if (components.length < 1) {
      return Optional.empty();
    }

    String commandName = components[0];
    Optional<CommandType> commandType = Command.toCommandType(commandName);
    if (!commandType.isPresent()) {
      return Optional.empty();
    }

    switch (commandType.get()) {
    case STORAGE:
      return toStorageCommand(components);
    case RETRIEVAL:
      return toRetrievalCommand(components);
    case DELETE:
      return toDeleteCommand(components);
    default:
      return Optional.empty();
    }
  }

  private Optional<Command> toStorageCommand(final String[] components) {
    return null;
  }

  @VisibleForTesting
  Optional<Command> toRetrievalCommand(final String[] components) {
    if (components.length < 2) {
      return Optional.empty();
    }

    List<String> keys = Arrays.asList(components).subList(1, components.length);
    return Optional.of(RetrievalCommand.of(components[0], keys));
  }

  @VisibleForTesting
  Optional<Command> toDeleteCommand(final String[] components) {
    if (components.length != 2) {
      return Optional.empty();
    }

    return Optional.of(DeleteCommand.of(components[0], components[1]));
  }
}
