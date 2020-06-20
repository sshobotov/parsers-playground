package com.github.sshobotov.credits

import java.io.{BufferedWriter, File, FileWriter}

import scala.util.Try

trait Writer[T] {
  def write(records: Iterator[T]): Either[Throwable, Unit]
}

object Writer {
  def stringsFile(target: File): Either[Throwable, Writer[String]] =
    Try(new BufferedWriter(new FileWriter(target)))
      .toEither
      .map { w =>
        new Writer[String] {
          override def write(records: Iterator[String]): Either[Throwable, Unit] =
            Try(records.foreach(w.write)).toEither
        }
      }
}
