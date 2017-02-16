# Context-Aware logging

This is a library that enables context-aware logging. It comes in handy when e.g.
your application handles multiple requests at the same time, and you want to be able
to see from each log entry to which request it belongs. It uses SLF4J to abstract from
the actual logging backend.

By default, it adds the runtime from the start of the flow, as well as the flow-id to the log entry.
Example:

```
21:09:30.123 INFO  com.rocketscience.Main.log - preparing start of the rocket 90c0c9a8-55da-403e-8ffc-d39d1c4a9190/10ms
21:09:30.460 INFO  com.rocketscience.Main.log - liftoff! 90c0c9a8-55da-403e-8ffc-d39d1c4a9190/750ms
21:09:30.517 INFO  com.rocketscience.Main.log - problems with the rocket motor 90c0c9a8-55da-403e-8ffc-d39d1c4a9190/807ms
21:09:30.518 INFO  com.rocketscience.Main.log - vibrations 90c0c9a8-55da-403e-8ffc-d39d1c4a9190/808ms
21:09:30.560 INFO  com.rocketscience.Main.log - BOOM! 90c0c9a8-55da-403e-8ffc-d39d1c4a9190/820ms
```

## Flow-ID?
It is just an ID that identifies anything that you consider a "flow". By default, UUIDs are used, but
you can create your own `Context` that includes another type, if you want to. A good example of a flow
is an incoming request, and I'd recommend to pass a flow id around as a header when starting HTTP requests.

## Usage
Generate a new logger for your class:

```scala
import de.lenabrueder.logging._

object Main {
  val log = Logger()  //automatically knows the name of the logger from the surrounding class and variable name
  
  def main(args:String*): Unit = {
    implicit val context:Context = JobContext("my fancy job")
    log.info("this is just a test")
  }
}
```
