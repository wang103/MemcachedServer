package memcachedserver.command;

import java.util.Optional;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import lombok.NonNull;

/**
 * This abstract class represents any possible memcached command.
 */
public abstract class Command {
  private static final Set<String> STORAGE_COMMANDS =
      ImmutableSet.of("set", "add", "replace", "append", "prepend");
  private static final Set<String> RETRIEVAL_COMMANDS =
      ImmutableSet.of("get");
  private static final String DELETE_COMMAND = "delete";

  @NonNull protected String name;

  public static Optional<CommandType> toCommandType(@NonNull final String commandName) {
    if (STORAGE_COMMANDS.contains(commandName)) {
      return Optional.of(CommandType.STORAGE);
    }

    if (RETRIEVAL_COMMANDS.contains(commandName)) {
      return Optional.of(CommandType.RETRIEVAL);
    }

    if (DELETE_COMMAND.equals(commandName)) {
      return Optional.of(CommandType.DELETE);
    }

    return Optional.empty();
  }
}
