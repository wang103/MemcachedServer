package memcachedserver.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * This class represents a delete command.
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class DeleteCommand extends Command {
  @NonNull private String key;

  private DeleteCommand(final String name, final String key) {
    super(name);
    this.key = key;
  }

  public static DeleteCommand of(@NonNull final String name, @NonNull final String key) {
    return new DeleteCommand(name, key);
  }
}
