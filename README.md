# BETest

## Why?

We are interested in your skills as a developer. As part of our assessment, we want to see your code.

## Instructions

In this repo, you'll find two files, Workbook2.csv and Workbook2.prn. These files need to be converted to a HTML format by the code you deliver. Please consider your work as a proof of concept for a system that can keep track of credit limits from several sources.

This repository is created specially for you, so you can push anything you like. Please update this README to provide instructions, notes and/or comments for us.

## The Constraints

Please complete the test within 5 business days. Use either Java, Scala or Kotlin. Use any libs / tools you like.

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

## Questions?

If you have any questions please send an email to DL-eCG-NL-assessment@ebay.com.

## Finished?

Please send an email to DL-eCG-NL-assessment@ebay.com let us know you're done.

Good Luck!


Copyright (C) 2001 - 2020 by Marktplaats BV an Ebay company. All rights reserved.
