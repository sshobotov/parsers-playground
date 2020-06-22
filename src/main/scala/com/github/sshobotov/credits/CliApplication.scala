package com.github.sshobotov.credits

import java.io.File
import java.nio.charset.Charset

import scala.io.{Codec, Source}

object CliApplication {
  def main(args: Array[String]): Unit = {
    val converters = Map(
        "csv" -> (Converter.csvToHtml, Charset.forName("ISO-8859-1"))
      , "prn" -> (Converter.prnToHtml, Charset.forName("ISO-8859-1"))
    )
    args.take(2) match {
      case Array(converterName, file) if converters.contains(converterName) =>
        val sourceFile = new File(file)

        if (!sourceFile.isFile) {
          System.err.println(s"Invalid file path $file")
        } else {
          val targetFile = File.createTempFile(sourceFile.getName, null)

          Sink.stringsFile(targetFile).flatMap {
            val (converter, charset) = converters(converterName)
            converter.pipe(Source.fromFile(sourceFile)(charset), _)
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
