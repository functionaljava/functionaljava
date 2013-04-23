import sbt._

object FJBuild extends Build {

  lazy val root = Project(id = "fj",
    base = file(".")) aggregate (core, demo)

  lazy val core = Project(id = "core",
    base = file("core"))

  lazy val demo = Project(id = "demo",
    base = file("demo")) dependsOn(core)
}
