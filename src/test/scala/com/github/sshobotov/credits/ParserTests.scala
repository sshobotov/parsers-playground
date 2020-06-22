package com.github.sshobotov.credits

import utest._

import scala.io.Source

object ParserTests extends TestSuite {
  val tests = Tests {
    test("NaivePrnParser") {
      test("should parse rows aka happy path") {
        val expect = List(
            List("Col 1" -> "111", "Column2" -> "B-B-B")
          , List("Col 1" -> "3333333", "Column2" -> "D D")
        )
        val result =
          (new Parser.NaivePrnParser)
            .parse(
                Source.fromString(
                  """Col 1  Column2
                    |111      B-B-B
                    |3333333    D D""".stripMargin)
              , List("Col 1", "Column2")
            )
            .map(_.toList)

        assert(result.isRight)
        assert(result.toTry.get == expect)
      }

      test("should parse rows from data with different order and with extra fields") {
        val expect = List(
            List("Col 1" -> "B-B-B", "Column2" -> "111")
          , List("Col 1" -> "D D", "Column2" -> "3333333")
        )
        val result =
          (new Parser.NaivePrnParser)
            .parse(
                Source.fromString(
                  """Column2 Col inside  Col 1 Col3
                    |111     1           B-B-B 3
                    |3333333 2             D D 4""".stripMargin)
              , List("Col 1", "Column2")
            )
            .map(_.toList)

        assert(result.isRight)
        assert(result.toTry.get == expect)
      }

      test("should parse no-value as empty string") {
        val expect = List(
            List("Col 1" -> "", "Column2" -> "B-B-B")
          , List("Col 1" -> "3333333", "Column2" -> "")
        )
        val result =
          (new Parser.NaivePrnParser)
            .parse(
                Source.fromString(
                  """Col 1  Column2
                    |         B-B-B
                    |3333333""".stripMargin)
              , List("Col 1", "Column2")
            )
            .map(_.toList)

        assert(result.isRight)
        assert(result.toTry.get == expect)
      }

      test("should return no data for empty rows") {
        val expect = List.empty[Map[String, String]]
        val result =
          (new Parser.NaivePrnParser)
            .parse(
                Source.fromString("""Col 1  Column2""")
              , List("Col 1", "Column2")
            )
            .map(_.toList)

        assert(result.isRight)
        assert(result.toTry.get == expect)
      }

      test("should fail if particular column is missing") {
        val result =
          (new Parser.NaivePrnParser)
            .parse(
                Source.fromString(
                  """Col 1  Column2
                    |111      B-B-B
                    |3333333    D D""".stripMargin)
              , List("Col 1", "Column3")
            )
            .map(_.toList)

        assert(result.isLeft)
      }
    }

    test("CsvParser") {
      test("should parse rows aka happy path") {
        val expect = List(
            List("Col1" -> "Connor, John", "Col, Two" -> "test value")
        )
        val result =
          new Parser.CsvParser()
            .parse(
                Source.fromString(
                  """Col1,"Col, Two"
                    |"Connor, John",test value""".stripMargin
                )
              , List("Col1", "Col, Two")
            )
            .map(_.toList)

        assert(result.isRight)
        assert(result.toTry.get == expect)
      }

      test("should parse rows from data with different order and with extra fields") {
        val expect = List(
            List("Col1" -> "test value", "Col, Two" -> "Connor, John")
        )
        val result =
          new Parser.CsvParser()
            .parse(
                Source.fromString(
                  """"Col, Two",ColInside,Col1,Tail Col
                    |"Connor, John",,test value,""".stripMargin
                )
              , List("Col1", "Col, Two")
            )
            .map(_.toList)

        assert(result.isRight)
        assert(result.toTry.get == expect)
      }

      test("should fail if particular column is missing") {
        val result =
          new Parser.CsvParser()
            .parse(
                Source.fromString(
                  """Col1,"Col, Two"
                    |"Connor, John",test value""".stripMargin
                )
              , List("Col1", "Col, Three")
            )
            .map(_.toList)

        assert(result.isLeft)
      }
    }
  }
}
