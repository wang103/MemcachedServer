# MemcachedServer

This is a toy memcached server inspired by [memcached protocol doc](https://github.com/memcached/memcached/blob/master/doc/protocol.txt).

The server runs on a single machine, and provides high-performant and high-concurrency in-memory caching solution.

## Build

Download the source code:
    $ git clone https://github.com/wang103/MemcachedServer.git
    $ cd MemcachedServer
    
Install [Apache Ant](https://ant.apache.org/) for building the source code.

Build the source code:
    $ ant

Start the server:
    $ ant MemcachedServerApp

Run all test cases:
    $ ant test

Clean the build:
    $ ant clean

## Future Works
