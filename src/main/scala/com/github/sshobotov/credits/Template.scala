package com.github.sshobotov.credits

import org.apache.commons.text.StringEscapeUtils

/**
 * Generates output with particular format for incoming data
 */
trait Template[T] {
  def render(rows: Iterator[T]): Either[Throwable, Iterator[String]]
}

object Template {
  class Table[T](
      prefix: String
    , header: T => String
    , row:    T => String
    , suffix: String
  ) extends Template[T] {
    def render(rows: Iterator[T]): Either[Throwable, Iterator[String]] = {
      val buf  = rows.buffered
      val init = buf.headOption match {
        case Some(r) => Iterator(prefix, header(r), row(r))
        case _       => Iterator(prefix)
      }
      Right {
        init
          .concat(rows.map(row(_)))
          .concat(Seq(suffix))
      }
    }
  }

  def htmlTable: Table[List[(String, String)]] = {
    def th(value: String): String =
      s"<th>${StringEscapeUtils.escapeHtml4(value)}</th>"

    def td(value: String): String =
      s"<td>${StringEscapeUtils.escapeHtml4(value)}</td>"

    new Table[List[(String, String)]](
        s"""<!DOCTYPE html>
           |<html>
           |  <body>
           |    <table>
           |""".stripMargin
      , r =>
        s"""      <tr>${r.map(_._1).map(th).mkString}</tr>
           |""".stripMargin
      , r =>
        s"""      <tr>${r.map(_._2).map(td).mkString}</tr>
           |""".stripMargin
      , s"""    </table>
           |  </body>
           |</html>""".stripMargin
    )
  }
}
