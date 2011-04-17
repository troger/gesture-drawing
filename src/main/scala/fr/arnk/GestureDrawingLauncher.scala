package fr.arnk

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.WebAppContext
import tools.nsc.io.{File, Directory}

object GestureDrawingLauncher {
  def main(args: Array[String]) {
    val server = new Server(8080);
    val webapp = new WebAppContext();
    webapp.setContextPath("/");

    // find the war file
    val warFile = Directory(".").deepFiles.find((f) => {
      f.name.endsWith(".war")
    })

    if (warFile.isDefined) {
      webapp.setWar(warFile.get.path);
      server.setHandler(webapp);
      server.start
      server.join
    }
  }
}
