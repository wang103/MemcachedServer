# MemcachedServer

This is a toy memcached server inspired by [memcached protocol doc](https://github.com/memcached/memcached/blob/master/doc/protocol.txt).

This project currently supports only a subset of all memcached functionalities.
For example, expiration is not supported right now.

The server runs on a single machine, and provides high-performant and high-concurrency in-memory caching solution.

## Build and Run

Download the source code:

    $ git clone https://github.com/wang103/MemcachedServer.git
    $ cd MemcachedServer

Install [Apache Ant](https://ant.apache.org/) for building the source code.

Build the source code:

    $ ant

Start the server:

    $ ant MemcachedServerApp
    or
    $ java -cp "bin:lib/*" memcachedserver.MemcachedServerApp -p 11211

The server accepts the following arguments:
    -p <port>        TCP port the server listens on. Default is 11211.
    -b <buckets>     The number of buckets in the backend data storage. More buckets, less lock contention. Default is 1000.
    -c <capacity>    The capacity for each bucket in the backend data storage. More capacity, less eviction. Default is 10000.

Run all test cases:

    $ ant test

Clean the build:

    $ ant clean

## Testing

To interact with the memcached server, open a terminal and connect to the server by using telnet:

    $ telnet localhost 11211

## Support Commands

Storage Commands

    - set
    - add
    - replace
    - append
    - prepend

Retrieval Commands

    - get

Other Commands

    - delete

## Future Works

    1. Fully support all memcached functionalities
    2. Make it support distributed setup
    3. Benchmark its performance (latency & bandwidth)
