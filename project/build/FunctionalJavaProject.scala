import sbt._
import java.util.jar.Attributes.Name._
import Process._

abstract class FunctionalJavaDefaults(info: ProjectInfo) extends DefaultProject(info) with JavaDocProject with OverridableVersion {
  private val encodingUtf8 = List("-encoding", "UTF-8")

  override def compileOptions = target(Target.Java1_5) :: List(CompileOptions.Unchecked, "-encoding", "UTF-8").map(CompileOption) ++ super.compileOptions

  override def javaCompileOptions = List("-target", "1.5", "-encoding", "UTF-8", "-Xlint:unchecked").map(JavaCompileOption) ++ super.javaCompileOptions

  override def mainJavaSourcePath = "src" / "main"

  override def testScalaSourcePath = "src" / "test"

  def scalacheckDependency = "org.scala-tools.testing" %% "scalacheck" % "1.8" % "test"

  override def testFrameworks = Seq(new TestFramework("org.scalacheck.ScalaCheckFramework"))

  override protected def disableCrossPaths = true

  override def packageSrcJar = defaultJarPath("-sources.jar")

  override def packageTestSrcJar = defaultJarPath("-test-sources.jar")

  override def outputPattern = "[conf]/[type]/[artifact](-[revision])(-[classifier]).[ext]"

  lazy val sourceArtifact = Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)

  override def documentOptions = encodingUtf8.map(SimpleDocOption(_)): List[ScaladocOption]

  override def managedStyle = ManagedStyle.Maven

  val authors = "Tony Morris, Runar Bjarnason, Tom Adams, Brad Clow, Ricky Clarkson, Jason Zaugg"

  val projectNameFull = "Functional Java"
  val projectUrl = "http://functionaljava.org/"

  override def packageOptions =
        ManifestAttributes(
          (IMPLEMENTATION_TITLE, projectNameFull)
          , (IMPLEMENTATION_URL, projectUrl)
          , (IMPLEMENTATION_VENDOR, authors)
          , (SEALED, "true")
        ) :: Nil

  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc, packageTestSrc, packageDocs)

  override protected def docAction = javadocAction

  override def javadocOptions = Seq(
    WindowTitle(projectNameFull + " " + version.toString)
    , DocTitle(<div><a href={projectUrl} target="_blank">{projectNameFull}</a> {version.toString} API Specification</div>.toString)
    , Header(<div><p><em>Copyright 2008 - 2010 {authors}</em></p>This software is released under an open source BSD licence.</div>.toString))

  override def consoleInit =
"""
import fj._
import fj.data._
import org.scalacheck._
import org.scalacheck.Prop._
"""
}

final class FunctionalJavaProject(info: ProjectInfo) extends ParentProject(info) with OverridableVersion {
  lazy val core = project("core", "functionaljava-core", new Core(_))
  lazy val fjscala = project("fjscala", "functionaljava-scala", new FJScala(_))

  class Core(info: ProjectInfo) extends FunctionalJavaDefaults(info) {
    val scalacheck = scalacheckDependency

    override def documentOptions = documentTitle("Functional Java") :: super.documentOptions
  }

  class FJScala(info: ProjectInfo) extends FunctionalJavaDefaults(info) {
    override def documentOptions = documentTitle("Functional Java for Scala") :: super.documentOptions
  }

}

trait JavaDocProject {
  self: DefaultProject =>

  sealed abstract class JavadocOption

  case class WindowTitle(t: String) extends JavadocOption

  case class DocTitle(t: String) extends JavadocOption

  case class Header(html: String) extends JavadocOption

  def javadocOptions = Seq[JavadocOption]()

  def javadocAction = javadocTask(mainLabel, mainSourceRoots, mainSources, mainDocPath, docClasspath, javadocOptions).dependsOn(compile) describedAs "Generate Javadoc"

  def javadocTask(label: String, sourceRoot: PathFinder, sources: PathFinder, outputDirectory: Path, classpath: PathFinder, options: Seq[JavadocOption]): Task = task {
    val os = options flatMap {
      case WindowTitle(t) => Seq("-windowtitle", t)
      case DocTitle(t) => Seq("-doctitle", t)
      case Header(html) => Seq("-header", html)
    }
    val proc = Process(Seq("javadoc", "-quiet", "-sourcepath", sourceRoot.get.toList.head.toString, "-d", outputDirectory.toString) ++ os ++ sources.getPaths)
    log.debug(proc.toString)
    proc !

    None
  }
}
