## Prerequisites

- Scala 2.13
- sbt 1.3

## How to use the code

To run tests
```
sbt test
```

To run with `sbt`
```
sbt `run csv <path_to_file>`
sbt `run prn <path_to_file>`
```

## Assumptions

- for PoC simple yet working solution is expected 
- despite it's a PoC flexibility and testability still is a thing
- files we operate may be very huge, so it's important to work with data in a streaming-style to save resources and
avoid OOM
- files have at least one row of data otherwise operation is meaningless and will fail
- it is required to convert data to common model (what usually is the case when you are dealing with data from multiple
sources) otherwise step of transformation could be removed

## Possible improvements

- data pipeline is build on top of `Iterator`s (for simplicity and lesser dependencies) but streaming libraries could be
used to have a better control of the data flows and declarative style of its definitions
- if common data model is required it's better to use `case classes` to describe it to have more type-safety
and generic programming tools (like `shapeless`) could be used to reduce boilerplate in this case
- html template is defined with a plain strings for simplicity, but it could be possible to use ADT or existing
libraries to have type-safe definition
- CLI application's arguments parsing uses `poor man's` solution, but existing libraries could be used to enrich user
experience
