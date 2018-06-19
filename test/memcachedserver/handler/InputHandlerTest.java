package memcachedserver.handler;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import memcachedserver.command.DeleteCommand;

public class InputHandlerTest {
  private static final String KEY = "key";

  private InputHandler inputHandler;

  @Before
  public void setUp() throws IOException {
    InputStream inputStream = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8));

    inputHandler = new InputHandler(inputStream);
  }

  @Test
  public void testToDeleteCommand() {
    String[] components = {"delete", KEY};
    assertEquals(Optional.of(DeleteCommand.of("delete", KEY)), inputHandler.toDeleteCommand(components));

    String[] invalidComponents = {"delete", KEY, "???"};
    assertEquals(Optional.empty(), inputHandler.toDeleteCommand(invalidComponents));
  }
}
