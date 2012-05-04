# graphie
A metric collection and graphing tool

Receives messages on a UDP port and calculates avg/max/min etc per second and allow you to draw graphs from that.

## Usage

```bash
lein deps
lein run
```

### Open UI
Goto http://localhost:8080/

### Open a connection to the UDP socket (ie. port 7890) and send messages on this format:
```
metric_name:123|v
```
The string should be encoded with UTF-8.

### Load testing
There is a JMeter load-test script called load-test.jmx in the project root,
it requires the plugins from http://code.google.com/p/jmeter-plugins/ for load testing the UDP receiving parts.
Seems to manage about 20000 incoming UDP messages on my Core2 Duo MacBook pro right now.

## TODO
* Docs on message format
* Calculate sum, max, min, avg, #samples (median?) for every second
* Storage (MongoDB or some circular buffer thing)
* Graph configuration parts
* Drop the message type?
* Load test

## License

Copyright (C) 2012 Jimmy Larsson

Distributed under the Eclipse Public License, the same as Clojure.
