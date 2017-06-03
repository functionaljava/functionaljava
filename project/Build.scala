import sbt._
import sbt.Keys._

object Build extends sbt.Build {
  override val settings = super.settings ++ Seq(
    name := "functionaljava",
    version := "4.1-SNAPSHOT",
    organization := "org.functionaljava",
    scalaVersion := "2.10.4",
    scalacOptions += "-target:jvm-1.7",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )

  lazy val core = Project(id = "core", base = file("core"), settings = settings ++ Project.defaultSettings)

  lazy val demo = Project(id = "demo", base = file("demo"), settings = settings ++ Project.defaultSettings) dependsOn core

  lazy val tests = Project(id = "tests", base = file("tests"), settings = settings ++ Project.defaultSettings) dependsOn core

  lazy val root = Project(id = "functionaljava", base = file("."), settings = settings ++ Project.defaultSettings) aggregate(core, demo)
}
