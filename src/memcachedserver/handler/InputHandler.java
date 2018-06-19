package memcachedserver.handler;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.annotations.VisibleForTesting;

import lombok.NonNull;
import memcachedserver.command.Command;
import memcachedserver.command.CommandType;
import memcachedserver.command.DeleteCommand;
import memcachedserver.command.RetrievalCommand;
import memcachedserver.command.StorageCommand;

/**
 * For handling input from one client.
 */
public class InputHandler implements Closeable {
  @NonNull private final BufferedReader bufferedReader;

  public InputHandler(@NonNull final InputStream inputStream) throws IOException {
    this.bufferedReader = new BufferedReader(
        new InputStreamReader(inputStream, StandardCharsets.UTF_8));
  }

  /**
   * Read a line and get the corresponding {@code Command}
   *
   * @return the {@code Command} if the line is a valid command line
   * @throws IOException if an I/O error occurs
   */
  public Optional<Command> readCommand() throws IOException {
    String line = bufferedReader.readLine();
    if (line == null) {
      throw new IOException("end of stream is reached");
    }

    String[] components = line.trim().split("\\s+");
    return toCommand(components);
  }

  /**
   * Read a line and get the corresponding data block
   *
   * @param len the size of the data block, excluding \r\n
   * @return the data block if valid
   * @throws IOException if an I/O error occurs
   */
  public Optional<Byte[]> readData(final int len) throws IOException {
    char[] buffer = new char[len + 2];
    int count = bufferedReader.read(buffer, 0, len + 2);

    if (count != len + 2 || buffer[count - 2] != '\r' || buffer[count - 1] != '\n') {
      return Optional.empty();
    }

    byte[] data = new String(buffer, 0, len).getBytes(StandardCharsets.UTF_8);
    return Optional.of(ArrayUtils.toObject(data));
  }

  @VisibleForTesting
  Optional<Command> toCommand(final String[] components) {
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

  @VisibleForTesting
  Optional<Command> toStorageCommand(final String[] components) {
    if (components.length != 5) {
      return Optional.empty();
    }

    int flags = Integer.valueOf(components[2]);
    int expireTime = Integer.valueOf(components[3]);
    int numBytes = Integer.valueOf(components[4]);

    return Optional.of(StorageCommand.of(components[0], components[1], flags, expireTime, numBytes));
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

  @Override
  public void close() throws IOException {
    bufferedReader.close();
  }
}
