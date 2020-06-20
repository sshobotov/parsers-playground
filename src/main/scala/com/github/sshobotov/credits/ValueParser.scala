package com.github.sshobotov.credits

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.util.Try

trait ValueParser[I, O] {
  def parse(in: I): Either[Throwable, O]
}

object ValueParser {
  object strings {
    val arbitrary: ValueParser[String, String] =
      raw => Right(raw)

    val positiveLong: ValueParser[String, String] =
      raw =>
        Try(raw.toLong)
          .toEither
          .flatMap {
            case num if num >= 0 => Right(num.toString)
            case num             => Left(new IllegalArgumentException(s"Expected positive number but got $num"))
          }

    def formattedDate(format: String): ValueParser[String, String] =
      raw =>
        Try(LocalDate.parse(raw, DateTimeFormatter.ofPattern(format)))
          .toEither
          .map(_.toString)
  }
}
