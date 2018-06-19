package memcachedserver.handler;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.ArrayUtils;

import lombok.NonNull;
import memcachedserver.store.Data;

/**
 * For handling output to one client.
 */
public class OutputHandler implements Closeable {
  @NonNull private final DataOutputStream output;

  public OutputHandler(@NonNull final OutputStream outputStream) throws IOException {
    this.output = new DataOutputStream(outputStream);
  }

  /**
   * Write a line to the client
   *
   * @param line the line excluding \r\n
   * @throws IOException if an I/O error occurs
   */
  public void writeLine(@NonNull final String line) throws IOException {
    output.writeBytes(line);
    writeEndOfLine();
  }

  /**
   * Write one data to the client
   *
   * @param key the key corresponding to the data
   * @param data the {@code Data}
   * @throws IOException if an I/O error occurs
   */
  public void writeData(@NonNull final String key, @NonNull final Data data) throws IOException {
    String infoLine = String.format("VALUE %s %d %d", key, data.flags(), data.data().length);
    writeLine(infoLine);

    String dataLine = new String(ArrayUtils.toPrimitive(data.data()), StandardCharsets.UTF_8);
    writeLine(dataLine);
  }

  private void writeEndOfLine() throws IOException {
    output.writeByte('\r');
    output.writeByte('\n');
  }

  @Override
  public void close() throws IOException {
    output.close();
  }
}
