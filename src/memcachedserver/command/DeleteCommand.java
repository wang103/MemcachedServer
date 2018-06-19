package memcachedserver.command;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * This class represents a delete command.
 */
@Value
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(staticName = "of")
public class DeleteCommand extends Command {
  @NonNull private String key;
}
