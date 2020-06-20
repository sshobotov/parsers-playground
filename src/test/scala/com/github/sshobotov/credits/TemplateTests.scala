package com.github.sshobotov.credits

import utest._

object TemplateTests extends TestSuite {
  val tests = Tests {
    def compact(lines: Iterator[String]): String =
      lines
        .flatMap(_.split("\n"))
        .map(_.trim)
        .mkString("\n")

    test("table template should build valid rows") {
      val expect =
        """<th><td>h1</td><td>h2</td></th>
          |<tr><td>r1</td><td>r2</td></tr>
          |<tr><td>r3</td><td>r4</td></tr>""".stripMargin
      val result =
        Template.htmlTable
          .render(Seq(
              Map("h1" -> "r1", "h2" -> "r2")
            , Map("h1" -> "r3", "h2" -> "r4")
          ).iterator)
          .map(compact)

      assert(result.isRight)
      assert(result.toTry.get.contains(expect))
    }

    test("html table template should escape html entities") {
      val expect =
        """<th><td>h1&quot;</td><td>h2</td></th>
          |<tr><td>r1</td><td>&lt;r2&gt;</td></tr>""".stripMargin
      val result =
        Template.htmlTable
          .render(Seq(
              Map("h1\"" -> "r1", "h2" -> "<r2>")
          ).iterator)
          .map(compact)

      assert(result.isRight)
      assert(result.toTry.get.contains(expect))
    }

    test("html table template should still build html for empty data") {
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
