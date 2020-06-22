package com.github.sshobotov.credits

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Try

/**
 * Converts values from one format/type into another
 */
trait ValueCodec[I, O] {
  def convert(in: I): Either[Throwable, O]
}

object ValueCodec {
  object strings {
    val arbitrary: ValueCodec[String, String] =
      raw => Right(raw)

    val positiveDouble: ValueCodec[String, String] =
      raw =>
        raw.toDoubleOption match {
          case Some(num) if num >= 0 => Right(raw)
          case _                     => Left(new IllegalArgumentException(s"Expected positive number but got $raw"))
        }

    def formattedDate(format: String): ValueCodec[String, String] =
      raw =>
        Try(LocalDate.parse(raw, DateTimeFormatter.ofPattern(format)))
          .toEither
          .map(_.toString)
  }
}
