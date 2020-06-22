package com.github.sshobotov.credits

import java.io.{BufferedWriter, File, FileWriter, PrintWriter}

import scala.util.Try

/**
 * End point of data pipeline, consumes incoming data
 */
trait Sink[T] {
  def consume(records: Iterator[T]): Either[Throwable, Unit]
}

object Sink {
  def stringsFile(target: File): Either[Throwable, Sink[String]] =
    Try(new PrintWriter(new BufferedWriter(new FileWriter(target))))
      .toEither
      .map { w =>
        new Sink[String] {
          override def consume(records: Iterator[String]): Either[Throwable, Unit] = {
            Try {
              try {
                records.foreach(w.println)
              } finally {
                w.close()
              }
            }.toEither
          }
        }
      }
}
