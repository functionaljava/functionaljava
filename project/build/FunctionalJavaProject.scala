import sbt._
import java.util.jar.Attributes.Name._
import Process._

abstract class FunctionalJavaDefaults(info: ProjectInfo) extends DefaultProject(info) with OverridableVersion {
  private val encodingUtf8 = List("-encoding", "UTF-8")

  override def compileOptions = target(Target.Java1_5) :: (CompileOptions.Unchecked :: encodingUtf8).map(CompileOption) ++ super.compileOptions

  override def javaCompileOptions = List("-source", "1.5", "-target", "1.5", "-encoding", "UTF-8", "-Xlint:unchecked").map(JavaCompileOption) ++ super.javaCompileOptions

  def scalacheckDependency = "org.scalacheck" %% "scalacheck" % "1.8" % "test"

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

  override def consoleInit =
"""
import fj._
import fj.data._
import org.scalacheck._
import org.scalacheck.Prop._
"""

  val pubishToRepoName = "Sonatype Nexus Repository Manager"

  val publishTo = {
    val repoUrl = "https://oss.sonatype.org/" + (if (version.toString.endsWith("-SNAPSHOT"))
      "content/repositories/snapshots"
    else
      "service/local/staging/deploy/maven2")

    pubishToRepoName at repoUrl
  }

  lazy val publishUser = system[String]("build.publish.user")
  lazy val publishPassword = system[String]("build.publish.password")

  (publishUser.get, publishPassword.get) match {
    case (Some(u), Some(p)) =>
      Credentials.add(pubishToRepoName, "nexus.scala-tools.org", u, p)
    case _ =>
      Credentials(Path.userHome / ".ivy2" / ".credentials", log)
  }
}

final class FunctionalJavaProject(info: ProjectInfo) extends ParentProject(info) with OverridableVersion {
  lazy val core = project("core", "functionaljava-core", new Core(_))
  lazy val demo = project("demo", "functionaljava-demo", new Demo(_), core)
  lazy val fjscala = project("fjscala", "functionaljava-scala", new FJScala(_), core)

  class Core(info: ProjectInfo) extends FunctionalJavaDefaults(info) with JavaDocProject {

    val scalacheck = scalacheckDependency

    override def testScalaSourcePath = "src" / "test"

    override protected def docAction = javadocAction

    override def javadocOptions = Seq(
      WindowTitle(projectNameFull + " " + version.toString)
      , DocTitle(<div><a href={projectUrl} target="_blank">{projectNameFull}</a> {version.toString} API Specification</div>.toString)
      , Header(<div><p><em>Copyright 2008 - 2011 {authors}</em></p>This software is released under an open source BSD licence.</div>.toString))

    override def documentOptions = documentTitle("Functional Java") :: super.documentOptions

    override def moduleID = "functionaljava"
  }

  class Demo(info: ProjectInfo) extends FunctionalJavaDefaults(info) {
    override def documentOptions = documentTitle("Functional Java Demonstration") :: super.documentOptions
  }

  class FJScala(info: ProjectInfo) extends FunctionalJavaDefaults(info) {
    override def documentOptions = documentTitle("Functional Java for Scala") :: super.documentOptions
  }

  private def noAction = task {None}

  override def deliverLocalAction = noAction

  override def publishLocalAction = noAction

  override def publishAction = noAction
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
