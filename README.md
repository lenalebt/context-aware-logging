# Context-Aware logging

This is a library that enables context-aware logging. It comes in handy when e.g.
your application handles multiple requests at the same time, and you want to be able
to see from each log entry to which request it belongs. It uses SLF4J to abstract from
the actual logging backend.

By default, it adds the runtime from the start of the trace, as well as the trace-id to the log entry.
Example:

```
21:09:30.123 INFO  com.rocketscience.Main.log - preparing start of the rocket 90c0c9a8-55da-403e-8ffc-d39d1c4a9190/10ms
21:09:30.460 INFO  com.rocketscience.Main.log - liftoff! 90c0c9a8-55da-403e-8ffc-d39d1c4a9190/750ms
21:09:30.517 INFO  com.rocketscience.Main.log - problems with the rocket motor 90c0c9a8-55da-403e-8ffc-d39d1c4a9190/807ms
21:09:30.518 INFO  com.rocketscience.Main.log - vibrations 90c0c9a8-55da-403e-8ffc-d39d1c4a9190/808ms
21:09:30.560 INFO  com.rocketscience.Main.log - BOOM! 90c0c9a8-55da-403e-8ffc-d39d1c4a9190/820ms
```

## Trace-ID?
It is just an ID that identifies anything that you consider a "trace". By default, UUIDs are used, but
you can create your own `Context` that includes another type, if you want to. A good example of a trace
is an incoming request, and I'd recommend to pass a trace id around as a header when starting HTTP requests.

## Usage
Add this to your `build.sbt`:

```
libraryDependencies ++= Seq(
    "de.lenabrueder" % "context-aware-logging" % "0.1-SNAPSHOT",
    "de.lenabrueder" % "context-aware-logging-play" % "2.6.0-SNAPSHOT", //only if you want play framework support
    "ch.qos.logback" % "logback-classic" % "1.2.1"
  )
```


Generate a new logger for your class:

```scala
import de.lenabrueder.logging._

object Main {
  val log = Logger()  //automatically knows the name of the logger from the surrounding class and variable name
  
  def main(args:String*): Unit = {
    implicit val context:Context = JobContext("my fancy job")
    log.info("this is just a test")
  }
  
  def process(data:String)(implicit context:Context):String = {
    log.info("will replace all the poo with unicorns!")
    data.replaceAll("💩", "🦄")
  }
}
```

might output

```
21:52:47 INFO  de.lenabrueder.logging.Main.log - this is just a test name=my fancy job 8441f0a9-3d7c-42a9-91e5-5a66523c3f6c/2ms
21:52:47 INFO  de.lenabrueder.logging.Main.log - will replace all the poo with unicorns! name=my fancy job 8441f0a9-3d7c-42a9-91e5-5a66523c3f6c/3ms
```

But you can easily create your own context types, `JobContext` simply is the only default one.

```scala
import de.lenabrueder.logging._

object Main {
  val log = Logger("my-special-logger") //give it a special name if you want to

  case class MyRequestContext(headers: Seq[(String, String)]) extends DefaultContextSettings {
    override def toMap: Map[String, String] =
      super.toMap.updated("headers", headers.map { case (k, v) => s"$k=$v" }.mkString(","))
  }

  def main(args: Array[String]): Unit = {
    implicit val context: Context = MyRequestContext(Seq("Host" -> "example.com", "Content-Length" -> "123"))
    log.info("bad example, who wants all headers in every log entry?")
  }
}

```

leads to

```
21:58:40 INFO  my-special-logger - bad example, who wants all headers in every log entry? headers=Host=example.com,Content-Length=123 27b48f15-006f-40e5-be60-4b8285d17b84/3ms
```

## Play framework support

You need to import `de.lenabrueder.logging.ImplicitConversions._` and can then `implicit val context: Context = request` in your Action.
This will automatically create an implicit context from the incoming request.

The version of the play support will follow the play versioning scheme in major and minor version. Patch version is up to the lib itself.

## TODO

* [ ] write better docs
* [ ] more testing (see point before this one)
* [ ] make travis build it
* [ ] make the output configurable
* [ ] add possibility to put the map on the thread MDC just before the log writing to allow customization via logger configuration
