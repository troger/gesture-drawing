import sbt._

class GestureDrawing(info: ProjectInfo) extends DefaultWebProject(info) {
  // Scalatra
  val scalatraVersion = "2.0.0.M3"
  val scalatra = "org.scalatra" %% "scalatra" % scalatraVersion
  val scalate = "org.scalatra" %% "scalatra-scalate" % scalatraVersion

  val slf4jBinding = "ch.qos.logback" % "logback-classic" % "0.9.25" % "runtime"

  // Configgy
  val configgy = "net.lag" % "configgy" % "2.0.0" % "compile" intransitive ()

  // jerkson
  val codaRepo = "Coda Hale's Repository" at "http://repo.codahale.com/"

  val jerkson = "com.codahale" %% "jerkson" % "0.1.7" withSources ()

  val jetty7WebApp = "org.eclipse.jetty" % "jetty-webapp" % "7.0.2.RC0" % "provided"
  val jetty7Plus = "org.eclipse.jetty" % "jetty-plus" % "7.0.2.RC0" % "provided"
  val servletApiDep = "javax.servlet" % "servlet-api" % "2.5" % "provided"

  override def runAction = task { args => runTask(getMainClass(true), runClasspath, args) dependsOn(compile, copyResources, packageAction) } describedAs BasicScalaProject.RunDescription
  override def mainClass = Some("fr.arnk.GestureDrawingLauncher")
}
