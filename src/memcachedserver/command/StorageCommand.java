package memcachedserver.command;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * This class represents any storage command, such as "set", "add", "replace".
 */
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class StorageCommand extends Command {
  @NonNull private String key;
  private int flags;
  private int expireTime;
  private int numBytes;

  private StorageCommand(
      final String name,
      final String key,
      final int flags,
      final int expireTime,
      final int numBytes) {
    super(name);
    this.key = key;
    this.flags = flags;
    this.expireTime = expireTime;
    this.numBytes = numBytes;
  }

  public static StorageCommand of(
      @NonNull final String name,
      @NonNull final String key,
      final int flags,
      final int expireTime,
      final int numBytes) {
    return new StorageCommand(name, key, flags, expireTime, numBytes);
  }
}
