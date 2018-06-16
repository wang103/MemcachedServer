package memcachedserver.store;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@RequiredArgsConstructor(staticName = "of")
public class Data {
  private final int flags;

  private final int expireTime;

  @NonNull private final byte[] data;
}
