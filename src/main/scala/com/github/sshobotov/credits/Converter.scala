package com.github.sshobotov.credits

import scala.io.Source

trait Converter[T] {
  def pipe(source: Source, writer: Writer[T]): Either[Throwable, Unit]
}

object Converter {
  type Column = String

  def prnToHtml: Converter[String] =
    columns(
        new Parser.NaivePrnParser
      ,  Map(
            "Name"         -> ValueParser.strings.arbitrary
          , "Birthday"     -> ValueParser.strings.formattedDate("yyyyMMdd")
          , "Address"      -> ValueParser.strings.arbitrary
          , "Postcode"     -> ValueParser.strings.arbitrary
          , "Phone"        -> ValueParser.strings.arbitrary
          , "Credit Limit" -> ValueParser.strings.positiveLong
        )
      , Template.htmlTable
    )

  def csvToHtml: Converter[String] =
    columns(
        new Parser.CsvParser()
      , Map(
            "Name"         -> ValueParser.strings.arbitrary
          , "Birthday"     -> ValueParser.strings.formattedDate("dd/MM/yyyy")
          , "Address"      -> ValueParser.strings.arbitrary
          , "Postcode"     -> ValueParser.strings.arbitrary
          , "Phone"        -> ValueParser.strings.arbitrary
          , "Credit Limit" -> ValueParser.strings.positiveLong
        )
      , Template.htmlTable
    )

  def columns[I, O, W](
      reader:   Parser[Map[Column, I]]
    , schema:   Map[Column, ValueParser[I, O]]
    , template: Template[Map[Column, O]]
  ): Converter[String] =
    new Converter[String] {
      override def pipe(source: Source, writer: Writer[String]): Either[Throwable, Unit] =
        for {
          in      <- reader.parse(source, schema.keysIterator.toList)
          unified  = in.map { record =>
            schema.foldLeft(Map.empty[Column, O]) { case (acc, (column, parser)) =>
              parser.parse(record(column)) match {
                case Right(parsed) => acc.updated(column, parsed)
                case Left(err)     => throw new IllegalStateException(s"Failed to parse $record", err)
              }
            }
          }
          out     <- template.render(unified)
          _       <- writer.write(out)
        } yield ()
    }
}
