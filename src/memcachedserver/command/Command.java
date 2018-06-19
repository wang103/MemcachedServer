package memcachedserver.command;

import lombok.NonNull;

/**
 * This abstract class represents any possible memcached command.
 */
public abstract class Command {
  @NonNull protected String name;
}
