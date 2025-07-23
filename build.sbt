ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "scala_esgi",
    libraryDependencies ++= Seq(
      "mysql" % "mysql-connector-java" % "8.0.33",
      "io.github.cdimascio" % "java-dotenv" % "5.2.2",
    ),
  )
