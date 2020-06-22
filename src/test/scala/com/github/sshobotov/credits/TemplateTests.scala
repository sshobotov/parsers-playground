package com.github.sshobotov.credits

import utest._

object TemplateTests extends TestSuite {
  val tests = Tests {
    def compact(lines: Iterator[String]): String =
      lines
        .flatMap(_.split("\n"))
        .map(_.trim)
        .mkString("\n")

    test("html table template") {
      test("should build valid rows") {
        val expect =
          """<tr><th>h1</th><th>h2</th></tr>
            |<tr><td>r1</td><td>r2</td></tr>
            |<tr><td>r3</td><td>r4</td></tr>""".stripMargin
        val result =
          Template.htmlTable
            .render(Seq(
                List("h1" -> "r1", "h2" -> "r2")
              , List("h1" -> "r3", "h2" -> "r4")
            ).iterator)
            .map(compact)

        assert(result.isRight)
        assert(result.toTry.get.contains(expect))
      }

      test("should escape html entities") {
        val expect =
          """<tr><th>h1&quot;</th><th>h2</th></tr>
            |<tr><td>r1</td><td>&lt;r2&gt;</td></tr>""".stripMargin
        val result =
          Template.htmlTable
            .render(Seq(
                List("h1\"" -> "r1", "h2" -> "<r2>")
            ).iterator)
            .map(compact)

        assert(result.isRight)
        assert(result.toTry.get.contains(expect))
      }

      test("should still build html for empty data") {
        val expect =
          """<table>
            |</table>""".stripMargin
        val result =
          Template.htmlTable
            .render(Iterator.empty)
            .map(compact)

        assert(result.isRight)
        assert(result.toTry.get.contains(expect))
      }
    }
  }
}
