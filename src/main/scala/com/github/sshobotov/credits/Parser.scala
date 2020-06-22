package com.github.sshobotov.credits

import com.github.tototoshi.csv._

import scala.io.Source
import scala.util.Try

/**
 * Reads the source and tries to parse records of expected type
 */
trait Parser[T] {
  def parse(source: Source, columns: List[String]): Parser.Results[T]
}

object Parser {
  type Results[T] = Either[Throwable, Iterator[T]]

  class NaivePrnParser extends Parser[List[(String, String)]] {
    override def parse(source: Source, columns: List[String]): Results[List[(String, String)]] =
      Try {
        val lines            = source.getLines()
        val headerLine       = lines.next()
        val columnBoundaries =
          columns.foldLeft(Map.empty[String, (Int, Int)]) { (acc, title) =>
            val startsOn = headerLine.indexOf(title)
            if (startsOn < 0)
              throw new IllegalArgumentException(s"Header $title from the source is missing")
            else {
              val checkAfter = startsOn + title.length
              val traversed  = headerLine.indexWhere(_ != ' ', checkAfter)
              val endsOn     = if (traversed == -1) headerLine.length else traversed

              acc.updated(title, (startsOn, endsOn))
            }
          }

        lines.map { line =>
          columns.foldRight(List.empty[(String, String)]) { case (column, acc) =>
            val (from, to) = columnBoundaries(column)
            (column, if (line.length <= from) "" else line.substring(from, to).trim) :: acc
          }
        }
      }.toEither
  }

  class CsvParser(format: CSVFormat = defaultCSVFormat) extends Parser[List[(String, String)]] {
    override def parse(source: Source, columns: List[String]): Results[List[(String, String)]] =
      Try {
        CSVReader
          .open(source)(format)
          .allWithHeaders()
          .iterator
          .buffered
      }.toEither.flatMap { it =>
        it.headOption match {
          case Some(record) =>
            val absentColumns = columns.filter(!record.contains(_))
            if (absentColumns.isEmpty) {
              val onlySelectedColumns =
                it.map { mapped =>
                  columns.foldRight(List.empty[(String, String)]) { (column, acc) =>
                    (column, mapped(column).trim) :: acc
                  }
                }
              Right(onlySelectedColumns)
            } else {
              Left(new IllegalStateException(s"Expected columns are absent: ${absentColumns.mkString(", ")}"))
            }

          case _ => Right(Iterator.empty[List[(String, String)]])
        }
      }
  }
}
