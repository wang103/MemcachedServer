package memcachedserver.command;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

public class CommandTest {
  @Test
  public void testToCommandType() {
    assertEquals(Optional.of(CommandType.STORAGE), Command.toCommandType("append"));
    assertEquals(Optional.of(CommandType.RETRIEVAL), Command.toCommandType("get"));
    assertEquals(Optional.of(CommandType.DELETE), Command.toCommandType("delete"));
    assertEquals(Optional.empty(), Command.toCommandType("garbage"));
  }
}
