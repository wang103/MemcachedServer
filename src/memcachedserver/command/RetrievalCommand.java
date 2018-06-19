package memcachedserver.command;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * This class represents any retrieval command, such as "get".
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class RetrievalCommand extends Command {
  @NonNull private List<String> keys;

  private RetrievalCommand(final String name, final List<String> keys) {
    super(name);
    this.keys = keys;
  }

  public static RetrievalCommand of(@NonNull final String name, @NonNull final List<String> keys) {
    return new RetrievalCommand(name, keys);
  }
}
