package memcachedserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import memcachedserver.handler.ClientHandler;
import memcachedserver.store.BucketedInMemoryLRUDataStore;
import memcachedserver.store.DataStore;

public class MemcachedServer {
  private static final String DEFAULT_PORT = "11211";
  private static final String DEFAULT_NUM_BUCKETS = "1000";
  private static final String DEFAULT_BUCKET_CAPACITY = "10000";

  private static final String PORT_OPTION = "p";
  private static final String NUM_BUCKETS_OPTION = "b";
  private static final String BUCKET_CAPACITY_OPTION = "c";

  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(PORT_OPTION, true, "TCP port the server listens on");
    options.addOption(NUM_BUCKETS_OPTION, true, "The number of buckets in the backend data storage");
    options.addOption(BUCKET_CAPACITY_OPTION, true, "The capacity for each bucket in the backend data storage");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmdLine = parser.parse(options, args);

    int port = Integer.valueOf(cmdLine.getOptionValue(PORT_OPTION, DEFAULT_PORT));
    int numBuckets = Integer.valueOf(cmdLine.getOptionValue(NUM_BUCKETS_OPTION, DEFAULT_NUM_BUCKETS));
    int bucketCapacity = Integer.valueOf(cmdLine.getOptionValue(BUCKET_CAPACITY_OPTION, DEFAULT_BUCKET_CAPACITY));

    DataStore dataStore = new BucketedInMemoryLRUDataStore(numBuckets, bucketCapacity);

    startServer(port, dataStore);
  }

  private static void startServer(final int port, final DataStore dataStore) throws IOException {
    ServerSocket ss = new ServerSocket(port);

    while (true) {
      Socket socket = ss.accept();

      Runnable clientHandler = new ClientHandler(socket, dataStore);

      new Thread(clientHandler).start();
    }
  }
}
