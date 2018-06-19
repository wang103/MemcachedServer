package memcachedserver.handler;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

import com.google.common.annotations.VisibleForTesting;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import memcachedserver.command.Command;
import memcachedserver.command.DeleteCommand;
import memcachedserver.command.RetrievalCommand;
import memcachedserver.command.StorageCommand;
import memcachedserver.store.Data;
import memcachedserver.store.DataStore;

/**
 * For handling communication with one client via {@link Socket}.
 * This class is responsible for closing the socket when it's no longer needed.
 */
@RequiredArgsConstructor
public class ClientHandler implements Runnable {
  private static final int MAX_KEY_LENGTH = 250;

  @NonNull private final Socket socket;
  @NonNull private final DataStore dataStore;
  @NonNull private final InputHandler inputHandler;
  @NonNull private final OutputHandler outputHandler;

  public ClientHandler(final Socket socket, final DataStore dataStore) throws IOException {
    this(
        socket,
        dataStore,
        new InputHandler(socket.getInputStream()),
        new OutputHandler(socket.getOutputStream()));
  }

  @Override
  public void run() {
    try {
      while (true) {
        Optional<Command> command = inputHandler.readCommand();

        if (!command.isPresent()) {
          outputHandler.writeLine("ERROR");
          continue;
        }

        handleCommand(command.get());
      }
    } catch (IOException e) {
      // ignore IOException, client closed its connection, so we just need to
      // release our resources and exit this thread
      System.out.println("Connection closed by client");
    } finally {
      releaseResources();
    }
  }

  private void handleCommand(final Command command) throws IOException {
    if (command instanceof StorageCommand) {
      handleStorageCommand((StorageCommand) command);
    } else if (command instanceof RetrievalCommand) {
      handleRetrievalCommand((RetrievalCommand) command);
    } else {
      handleDeleteCommand((DeleteCommand) command);
    }
  }

  @VisibleForTesting
  void handleStorageCommand(final StorageCommand command) throws IOException {
    if (command.key().length() > MAX_KEY_LENGTH) {
      outputHandler.writeLine("CLIENT_ERROR bad command line format");
      return;
    }

    Optional<Byte[]> data = inputHandler.readData(command.numBytes());
    if (!data.isPresent()) {
      outputHandler.writeLine("CLIENT_ERROR bad data chunk");
      return;
    }

    boolean success = true;

    if (command.name().equals("set")) {
      dataStore.set(command.key(), Data.of(command.flags(), command.expireTime(), data.get()));
    } else if (command.name().equals("add")) {
      success = dataStore.add(command.key(), Data.of(command.flags(), command.expireTime(), data.get()));
    } else if (command.name().equals("replace")) {
      success = dataStore.replace(command.key(), Data.of(command.flags(), command.expireTime(), data.get()));
    } else if (command.name().equals("append")) {
      success = dataStore.append(command.name(), data.get());
    } else if (command.name().equals("prepend")) {
      success = dataStore.prepend(command.name(), data.get());
    }

    if (success) {
      outputHandler.writeLine("STORED");
    } else {
      outputHandler.writeLine("NOT_STORED");
    }
  }

  @VisibleForTesting
  void handleRetrievalCommand(final RetrievalCommand command) throws IOException {
    Map<String, Data> keyToData = dataStore.get(command.keys());

    for (String key : command.keys()) {
      Data data = keyToData.get(key);

      if (data != null) {
        outputHandler.writeData(key, data);
      }
    }

    outputHandler.writeLine("END");
  }

  @VisibleForTesting
  void handleDeleteCommand(final DeleteCommand command) throws IOException {
    if (dataStore.delete(command.key())) {
      outputHandler.writeLine("DELETED");
    } else {
      outputHandler.writeLine("NOT_FOUND");
    }
  }

  private void releaseResources() {
    try {
      inputHandler.close();
      outputHandler.close();
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
