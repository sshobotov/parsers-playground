package com.github.sshobotov.credits

import java.io.File

import scala.io.Source

object CliApplication {
  def main(args: Array[String]): Unit = {
    val converters = Map(
        "csv" -> Converter.csvToHtml
      , "prn" -> Converter.prnToHtml
    )
    args.take(2) match {
      case Array(converter, file) if converters.contains(converter) =>
        val sourceFile = new File(file)
        if (!sourceFile.isFile) throw new IllegalArgumentException(s"Invalid file path $file")
        else {
          val targetFile = File.createTempFile(sourceFile.getName, null)
          Writer.stringsFile(targetFile).flatMap {
            converters(converter).pipe(Source.fromFile(sourceFile), _)
          } match {
            case Left(err) => throw err
            case _         => println(s"Copied to ${targetFile.getAbsolutePath}")
          }
        }

      case _ =>
        println(
          s"""
            |Unexpected arguments, supports only:
            |${converters.keys.map(name => s"> $name <file-path>").mkString("\n")}""".stripMargin)
    }
  }
}
