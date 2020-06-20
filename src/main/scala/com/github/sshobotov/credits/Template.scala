package com.github.sshobotov.credits

import org.apache.commons.text.StringEscapeUtils

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

  def htmlTable: Table[Map[String, String]] = {
    def htmlTableCell(value: String): String =
      s"<td>${StringEscapeUtils.escapeHtml4(value)}</td>"

    new Table[Map[String, String]](
        s"""<!doctype html>
            |<html>
            |  <body>
            |    <table>""".stripMargin
      , r =>
        s"""       <th>${r.keys.map(htmlTableCell).mkString}</th>""".stripMargin
      , r =>
        s"""       <tr>${r.values.map(htmlTableCell).mkString}</tr>""".stripMargin
      , s"""     </table>
            |  </body>
            |</html>""".stripMargin
    )
  }
}
