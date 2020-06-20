name := "converters"

version := "0.1"

scalaVersion := "2.13.2"

libraryDependencies ++= Seq(
  "com.github.tototoshi" %% "scala-csv"    % "1.3.6",
  "org.apache.commons"   %  "commons-text" % "1.8",

  "com.lihaoyi"          %% "utest"        % "0.7.2" % "test",
)

mainClass in (Compile, run) := Some("com.github.sshobotov.credits.CliApplication")

testFrameworks += new TestFramework("utest.runner.Framework")
