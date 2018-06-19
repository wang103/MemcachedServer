package memcachedserver.command;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * This class represents any storage command, such as "set", "add", "replace".
 */
@Value
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(staticName = "of")
public class StorageCommand extends Command {
  @NonNull private String key;
  private int flags;
  private int expireTime;
  private int numBytes;
}
