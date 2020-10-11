[![Build Status](https://travis-ci.org/gumartinm/spark-shared-spark-session-helper.svg?branch=master)](https://travis-ci.org/gumartinm/spark-shared-spark-session-helper)


Spark: unit, integration and end to end tests.
=========================


## Building

### Prerequisites

In order to build this project [**sbt**](https://www.scala-sbt.org/download.html) and [**JVM 8**](https://adoptopenjdk.net/?variant=openjdk8&jvmVariant=hotspot) must be available in your environment.

### Command

```
sbt clean compile scalastyle coverage test test:scalastyle coverageReport
```

## Assembly, generate jar file

```
sbt assembly
```

