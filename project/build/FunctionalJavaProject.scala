import sbt._
import java.util.jar.Attributes.Name._

final class FunctionalJavaProject(info: ProjectInfo) extends DefaultProject(info) {
  override def compileOptions = target(Target.Java1_5) :: List(CompileOptions.Unchecked,  "-encoding", "UTF-8").map(CompileOption) ++ super.compileOptions

  override def javaCompileOptions = List("-target", "1.5", "-encoding", "UTF-8", "-Xlint:unchecked").map(JavaCompileOption) ++ super.javaCompileOptions

  override def packageOptions =
    ManifestAttributes(
                        (IMPLEMENTATION_TITLE, "Functional Java")
                      , (IMPLEMENTATION_URL, "http://functionaljava.org/")
                      , (IMPLEMENTATION_VENDOR, "Tony Morris, Runar Bjarnason, Tom Adams, Brad Clow, Ricky Clarkson, Jason Zaugg")
                      , (SEALED, "true")
                      ) :: Nil

  override protected def disableCrossPaths = true

  override def mainJavaSourcePath = "src" / "main"

  override def testScalaSourcePath = "src" / "test"

  override def managedStyle = ManagedStyle.Maven

  override def packageSrcJar = defaultJarPath("-sources.jar")

  override def packageTestSrcJar = defaultJarPath("-test-sources.jar")

  override def outputPattern = "[conf]/[type]/[artifact](-[revision])(-[classifier]).[ext]"

  lazy val sourceArtifact = Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)

  val scalacheckDependency = "org.scala-tools.testing" %% "scalacheck" % "1.8" % "test"

  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc, packageTestSrc)

  override def testFrameworks = Seq(new TestFramework("org.scalacheck.ScalaCheckFramework"))

  override def consoleInit =
"""
import fj._
import fj.data._
import org.scalacheck._
import org.scalacheck.Prop._
"""

}
