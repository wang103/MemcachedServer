package memcachedserver.handler;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;

import lombok.NonNull;
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
public class ClientHandler implements Runnable {
  @NonNull private final Socket socket;
  @NonNull private final DataStore dataStore;
  @NonNull private final InputHandler inputHandler;
  @NonNull private final OutputHandler outputHandler;

  public ClientHandler(@NonNull final Socket socket, @NonNull final DataStore dataStore) throws IOException {
    this.socket = socket;
    this.dataStore = dataStore;
    this.inputHandler = new InputHandler(socket.getInputStream());
    this.outputHandler = new OutputHandler(socket.getOutputStream());
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

  private void handleStorageCommand(final StorageCommand command) throws IOException {
    Optional<Byte[]> data = inputHandler.readData(command.numBytes());
    if (!data.isPresent()) {
      outputHandler.writeLine("CLIENT_ERROR bad data chunk");
      return;
    }

    if (command.name().equals("set")) {
      dataStore.set(command.key(), Data.of(command.flags(), command.expireTime(), data.get()));
      outputHandler.writeLine("STORED");
    } else if (command.name().equals("add")) {
      if (dataStore.add(command.key(), Data.of(command.flags(), command.expireTime(), data.get()))) {
        outputHandler.writeLine("STORED");
      } else {
        outputHandler.writeLine("NOT_STORED");
      }
    } else if (command.name().equals("replace")) {
      if (dataStore.replace(command.key(), Data.of(command.flags(), command.expireTime(), data.get()))) {
        outputHandler.writeLine("STORED");
      } else {
        outputHandler.writeLine("NOT_STORED");
      }
    } else if (command.name().equals("append")) {
      if (dataStore.append(command.name(), data.get())) {
        outputHandler.writeLine("STORED");
      } else {
        outputHandler.writeLine("NOT_STORED");
      }
    } else if (command.name().equals("prepend")) {
      if (dataStore.prepend(command.name(), data.get())) {
        outputHandler.writeLine("STORED");
      } else {
        outputHandler.writeLine("NOT_STORED");
      }
    }
  }

  private void handleRetrievalCommand(final RetrievalCommand command) throws IOException {
    Map<String, Data> keyToData = dataStore.get(command.keys());

    for (String key : command.keys()) {
      Data data = keyToData.get(key);

      if (data != null) {
        outputHandler.writeData(key, data);
      }
    }

    outputHandler.writeLine("END");
  }

  private void handleDeleteCommand(final DeleteCommand command) throws IOException {
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
