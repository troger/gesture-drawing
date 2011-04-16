package fr.arnk

import org.scalatra.scalate.ScalateSupport
import org.scalatra.{ScalatraServlet, UrlSupport}
import com.codahale.jerkson.Json._
import Configuration._
import java.io.{File => JFile, FileInputStream}

class GestureDrawingServlet extends ScalatraServlet with ScalateSupport with UrlSupport {

  import fr.arnk.GestureDrawingServlet._

  before {
    contentType = "text/html"
  }

  protected def validPicturesPath: Boolean = {
    val configFile = new JFile(ConfigFilePath)
    if (!configFile.exists()) {
      new JFile(ConfigFolderPath).mkdirs()
      configFile.createNewFile()
      return false
    }
    val properties = new java.util.Properties()
    properties.load(new FileInputStream(configFile))
    val picturesPath = properties.getProperty("pictures.path")
    val picturesPathFile = new JFile(picturesPath)
    if (!picturesPathFile.exists() || !picturesPathFile.isDirectory) return false
    return true
  }

  get("/") {
    if (!validPicturesPath) {
      renderTemplate("/WEB-INF/no_valid_pictures_path.ssp", ("configFilePath" -> ConfigFilePath))
    } else {
      val categories = getCategories
      categories match {
        case Some(c) => renderTemplate("/WEB-INF/index.ssp", ("categories" -> c), ("postUrl" -> url("/slideshow")))
        case None => renderTemplate("/WEB-INF/error.ssp")
      }
    }

  }

  post("/slideshow") {
    val allPictures = getPictures
    allPictures match {
      case None => renderTemplate("/WEB-INF/error.ssp")
      case Some(picts) => {
        def categoryMatch(list: List[String], kv: Tuple2[String, List[String]]): List[String] = {
          if (multiParams("category").exists((c) => kv._1.startsWith(c))) {
            kv._2.map(kv._1 + _) ++ list
          } else {
            list
          }
        }
        val pictures = scala.util.Random.shuffle(picts.foldLeft(List[String]())(categoryMatch))
        renderTemplate("/WEB-INF/slideshow.ssp", ("baseURL" -> url("/picture/")), ("pictures" -> generate(pictures)), ("timeInterval" -> params("timeInterval").toInt))
      }
    }
  }

  get("/picture/*") {
    contentType = "image/jpeg"
    new java.io.File(getPicturesPath.get + multiParams("splat")(0))
  }

  get("/reload") {
    reloadPicturesCache()
    redirect(url("/"))
  }

  get("/js/*") {
    servletContext.getNamedDispatcher("default").forward(request, response)
  }

  get("/css/*") {
    servletContext.getNamedDispatcher("default").forward(request, response)
  }

  protected def contextPath = request.getContextPath

}

object GestureDrawingServlet {

  val picturesCacheManager = new PicturesCacheManager

  def getPictures = {
    loadPicturesCache()
    picturesCacheManager.getPictures
  }

  def getCategories = {
    loadPicturesCache()
    picturesCacheManager.getCategories
  }

  def getPicturesPath = {
    loadPicturesCache()
    picturesCacheManager.getPicturesPath
  }

  def reloadPicturesCache() {
    picturesCacheManager.reload()
  }

  protected def loadPicturesCache() {
    if (!picturesCacheManager.picturesCacheLoaded) {
      val configFile = new JFile(ConfigFilePath)
      val picturesPath = configFile.exists match {
        case true => getPicturesPathFromConfigFile(configFile)
        case false => "." + JFile.separator
      }
      picturesCacheManager.load(picturesPath)
    }
  }

  protected def getPicturesPathFromConfigFile(f: JFile): String = {
    val properties = new java.util.Properties()
    properties.load(new FileInputStream(f))
    val picturesPath = properties.getProperty("pictures.path")
    if (!picturesPath.endsWith(JFile.separator)) {
      picturesPath + JFile.separator
    } else {
      picturesPath
    }
  }

}
