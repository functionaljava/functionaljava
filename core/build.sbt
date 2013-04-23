name := "functionaljava"

version := "3.2-SNAPSHOT"

organization := "org.functionaljava"

javacOptions ++= Seq("-target", "1.5", "-source", "1.5", "-encoding", "UTF-8", "-Xlint:unchecked")

crossPaths := false

autoScalaLibrary := false

publishMavenStyle := true

publishArtifact in Test := false

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://functionaljava.org</url>
  <licenses>
    <license>
      <name>BSD-style</name>
      <url>http://www.opensource.org/licenses/bsd-license.php</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:functionaljava/functionaljava.git</url>
    <connection>scm:git:git@github.com:functionaljava/functionaljava.git</connection>
  </scm>
  <developers>
    {
       Seq(
         ("tonymorris", "Tony Morris"),
         ("runarorama", "Runar Bjarnason"),
         ("tomjadams", "Tom Adams"),
         ("bradclow", "Brad Clow"),
         ("rickyclarkson", "Ricky Clarkson")
       ).map {
         case (id, name) =>
           <developer>
             <id>{id}</id>
             <name>{name}</name>
             <url>http://github.com/{id}</url>
           </developer>
       }
    }
  </developers>
)

libraryDependencies ++= Seq(
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
)
