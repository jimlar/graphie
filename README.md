# graphie
A metric collection and graphing tool

Receives messages on a UDP port and calculates avg/max/min etc per second and allow you to draw graphs from that.

## Usage

```bash
lein deps
lein run
```

### Open UI
Goto http://localhost:3000/

### Open a connection to the UDP socket (ie. port 7890) and send messages on this format:
```
metric_name:123|v
```
The string should be encoded with UTF-8. The max length of the message (after UTF-8 encoding) is 100 bytes.

### Load testing
There is a JMeter load-test script called load-test.jmx in the project root,
it requires the plugins from http://code.google.com/p/jmeter-plugins/ for load testing the UDP receiving parts.
Seems to manage about 80000 incoming UDP messages on my i7 Lenovo X230 right now.

## TODO
* Websockets for the live graph
* Graph configuration parts
* Drop the message type?
* Periodic cleanup of seconds (if input stops)
* Use D3 for the graphing instead

## License

Copyright (C) 2012 Jimmy Larsson

Distributed under the Eclipse Public License, the same as Clojure.
