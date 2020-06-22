package com.github.sshobotov.credits

import utest._

import scala.io.Source
import scala.util.Try

object ConverterTests extends TestSuite {
  val tests = Tests {
    test("Converter") {
      val cartesianParser: Parser[List[(String, String)]] =
        (source, columns) => Right(
          source.getLines.map(raw => columns.map(_ -> raw))
        )

      val joinTemplate: Template[List[(String, String)]] =
        it => Right(
          it.map(_.map { case (k, v) => s"$k:$v" }.mkString(","))
        )

      def recordingSink = new Sink[String] {
        private var lastConsumed: List[String] = List.empty

        override def consume(records: Iterator[String]): Either[Throwable, Unit] =
          Try(records.toList)
            .toEither
            .map { lastConsumed = _ }

        def read: List[String] = lastConsumed
      }

      test("columnar should apply schema and template") {
        val sink = recordingSink
        val done =
          Converter.columnar(
              cartesianParser
            , List(
                  "c1" -> ValueCodec.strings.arbitrary
                , "c2" -> ValueCodec.strings.arbitrary
              )
            , joinTemplate
          ).pipe(
              Source.fromString(
                """v1
                  |v2""".stripMargin
              )
            , sink
          )
        assert(done.isRight)

        val expect = List(
            "c1:v1,c2:v1"
          , "c1:v2,c2:v2"
        )
        val result = sink.read
        assert(result == expect)
      }

      test("columnar should handle gracefully codec application failure") {
        val sink = recordingSink
        val done =
          Converter.columnar(
              cartesianParser
            , List("c1" -> ValueCodec.strings.positiveDouble)
            , joinTemplate
          ).pipe(
              Source.fromString(
                """v1
                  |v2""".stripMargin
              )
            , sink
          )
        assertMatch(done) {
          case Left(_: Converter.SchemaCodecError[_]) =>
        }
      }

      test("columnar should handle gracefully empty source") {
        val sink = recordingSink
        val done =
          Converter.columnar(
              cartesianParser
            , List("c1" -> ValueCodec.strings.arbitrary)
            , joinTemplate
          ).pipe(
              Source.fromIterable(Seq.empty[Char])
            , sink
          )
        assertMatch(done) {
          case Left(Converter.NoDataError) =>
        }
      }
    }
  }
}
