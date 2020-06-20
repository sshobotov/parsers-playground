package com.github.sshobotov.credits

import utest._

import scala.io.Source

object ParserTests extends TestSuite {
  val tests = Tests {
    test("NaivePrnParser") {
      test("should parse file aka happy path") {
        val expect = List(
            Map("Col 1" -> "111", "Column2" -> "B-B-B")
          , Map("Col 1" -> "3333333", "Column2" -> "D D")
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
    }

    test("CsvParser") {
      val expect = List(
          Map("Col1" -> "Connor, John", "Col, Two" -> "test value")
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
  }
}
