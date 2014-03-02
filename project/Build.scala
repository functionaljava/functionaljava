import sbt._
import Keys._

object Build extends Build {
  
  lazy val defaultSettings =
    Defaults.defaultSettings ++
      Seq(
        version := "1.0",
        scalaVersion := "2.10.3",
		javacOptions := Seq("-source", "1.8"),
        scalacOptions := Seq(
          "-feature",
          "-unchecked",
          "-deprecation",
		  "-language:implicitConversions",
          "-language:postfixOps",
          "-encoding", "utf8",
          "-Ywarn-adapted-args"
        ),
		scalaSource in Test <<= (baseDirectory in Test)(_/"src/test")
      )
  
  lazy val core = Project("core",
    file("core"),
	
    settings = defaultSettings ++ Seq(
    resolvers ++= Seq(
		"Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
	),
	libraryDependencies ++= Seq(
	  "org.scalacheck" %% "scalacheck" % "1.11.3" % "test"
	)
    )
  )
    
  
  lazy val demo = Project("demo",
    file("demo"),
    settings = defaultSettings ++ Seq(
		javaSource in Compile <<= (baseDirectory in Compile)(_/"src")
	)
  ).dependsOn(core)
}
