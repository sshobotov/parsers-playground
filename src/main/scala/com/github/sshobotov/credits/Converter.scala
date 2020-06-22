package com.github.sshobotov.credits

import scala.io.Source
import scala.util.Try

/**
 * Tries to convert the data from input source into output sink
 * hides all the logic of applied transformation steps
 */
trait Converter[T] {
  def pipe(source: Source, sink: Sink[T]): Either[Throwable, Unit]
}

object Converter {
  type Column = String
  type Record[T] = List[(Column, T)]
  type Schema[I, O] = List[(Column, ValueCodec[I, O])]

  case object NoDataError extends Exception("Empty source can't be converted")

  class SchemaCodecError[T](record: T, column: String, reason: Throwable) extends
    Exception(s"Failed to apply `$column` codec to $record", reason)

  def prnToHtml: Converter[String] = {
    import ValueCodec.strings

    columnar(
        new Parser.NaivePrnParser
      , List(
            "Name"         -> strings.arbitrary
          , "Birthday"     -> strings.formattedDate("yyyyMMdd")
          , "Address"      -> strings.arbitrary
          , "Postcode"     -> strings.arbitrary
          , "Phone"        -> strings.arbitrary
          , "Credit Limit" -> strings.positiveDouble
        )
      , Template.htmlTable
    )
  }

  def csvToHtml: Converter[String] = {
    import ValueCodec.strings

    columnar(
        new Parser.CsvParser()
      , List(
            "Name"         -> strings.arbitrary
          , "Birthday"     -> strings.formattedDate("dd/MM/yyyy")
          , "Address"      -> strings.arbitrary
          , "Postcode"     -> strings.arbitrary
          , "Phone"        -> strings.arbitrary
          , "Credit Limit" -> strings.positiveDouble
        )
      , Template.htmlTable
    )
  }

  def columnar[I, O, W](
      parser:   Parser[Record[I]]
    , schema:   Schema[I, O]
    , template: Template[Record[O]]
  ): Converter[String] =
    new Converter[String] {
      override def pipe(source: Source, sink: Sink[String]): Either[Throwable, Unit] =
        for {
          in          <- parser.parse(source, schema.map(_._1))
          _           <- Either.cond(in.nonEmpty, (), NoDataError)
          unifyRow     = transform[I, O](_, schema)
          unifyOrFail  = unifyRow.andThen(_.get)
          unified      = in.map(unifyOrFail)
          out         <- template.render(unified)
          _           <- sink.consume(out)
        } yield ()
    }

  private def transform[I, O](record: Record[I], schema: Schema[I, O]): Try[Record[O]] =
    Try {
      val mapping = record.toMap

      schema.foldRight(List.empty[(Column, O)]) { case ((column, codec), acc) =>
        codec.convert(mapping(column)) match {
          case Right(parsed) => (column, parsed) :: acc
          case Left(err)     => throw new SchemaCodecError(record, column, err)
        }
      }
    }
}
