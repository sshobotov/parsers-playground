package com.github.sshobotov.credits

import com.github.tototoshi.csv._

import scala.io.Source
import scala.util.Try

trait Parser[T] {
  def parse(source: Source, columns: List[String]): Parser.Results[T]
}

object Parser {
  type Results[T] = Either[Throwable, Iterator[T]]

  class NaivePrnParser extends Parser[Map[String, String]] {
    override def parse(source: Source, columns: List[String]): Results[Map[String, String]] =
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
          columns.foldLeft(Map.empty[String, String]) { case (acc, column) =>
            val (from, to) = columnBoundaries(column)
            acc.updated(column, line.substring(from, to).trim)
          }
        }
      }.toEither
  }

  class CsvParser(format: CSVFormat = defaultCSVFormat) extends Parser[Map[String, String]] {
    override def parse(source: Source, columns: List[String]): Results[Map[String, String]] =
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
                  columns.foldLeft(Map.empty[String, String]) { (acc, column) =>
                    acc.updated(column, mapped(column).trim)
                  }
                }
              Right(onlySelectedColumns)
            } else {
              Left(new IllegalStateException(s"Expected columns are absent: ${absentColumns.mkString(", ")}"))
            }

          case _ => Right(it)
        }
      }
  }
}
