package memcachedserver.command;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * This class represents any retrieval command, such as "get".
 */
@Value
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor(staticName = "of")
public class RetrievalCommand extends Command {
  @NonNull private List<String> keys;
}
