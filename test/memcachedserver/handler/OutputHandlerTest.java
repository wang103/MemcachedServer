package memcachedserver.handler;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Before;
import org.junit.Test;

import memcachedserver.store.Data;

public class OutputHandlerTest {
  private static final String INPUT_LINE = "hello";

  private static final String KEY = "key";
  private static final byte[] BYTES = "daf4534*^(:{}>?<dasf3413~43adf".getBytes(StandardCharsets.UTF_8);
  private static final Data DATA = Data.of(100, 0, ArrayUtils.toObject(BYTES));

  private ByteArrayOutputStream outputStream;
  private OutputHandler outputHandler;

  @Before
  public void setUp() throws IOException {
    outputStream = new ByteArrayOutputStream();
    outputHandler = new OutputHandler(outputStream);
  }

  @Test
  public void testWriteLine() throws IOException {
    outputHandler.writeLine(INPUT_LINE);

    byte[] bytes = outputStream.toByteArray();
    String actual = new String(bytes, StandardCharsets.UTF_8);

    assertEquals(INPUT_LINE + "\r\n", actual);
  }

  @Test
  public void testWriteData() throws IOException {
    outputHandler.writeData(KEY, DATA);

    byte[] bytes = outputStream.toByteArray();
    String actual = new String(bytes, StandardCharsets.UTF_8);

    String expected = String.format(
        "VALUE %s %d %d\r\n%s\r\n", KEY, 100, BYTES.length, new String(BYTES, StandardCharsets.UTF_8));
    assertEquals(expected, actual);
  }
}
