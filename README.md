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
metric_name:123|ms
```
The string should be encoded with UTF-8.


## TODO
* Docs on message format
* Calculate sum, max, min, avg, #samples (median?) for every second
* Storage (MongoDB or some circular buffer thing)
* Graph configuration parts

## License

Copyright (C) 2012 Jimmy Larsson

Distributed under the Eclipse Public License, the same as Clojure.
