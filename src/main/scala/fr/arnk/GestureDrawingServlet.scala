package fr.arnk

import org.scalatra.scalate.ScalateSupport
import org.scalatra.{ScalatraServlet, UrlSupport}
import com.codahale.jerkson.Json._
import Configuration._
import tools.nsc.io.File

class GestureDrawingServlet extends ScalatraServlet with ScalateSupport with UrlSupport {

  import fr.arnk.GestureDrawingServlet._

  before {
    contentType = "text/html"
    configure()
  }

  protected def validPicturesPath: Boolean = {
    val picturesPath = config.getString("pictures_path")
    picturesPath match {
      case Some(pp) => {
        val picturesPathFile = File(pp)
        if (!picturesPathFile.exists || !picturesPathFile.isDirectory) return false
        return true
      }
      case None => return false
    }

  }

  get("/") {
    if (!configFileExists || !validPicturesPath) {
      renderTemplate("/WEB-INF/no_valid_pictures_path.ssp", ("configFilePath" -> ConfigFilePath), ("reloadUrl" -> url("/reload")))
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
    config.reload()
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
      val configFile = File(ConfigFilePath)
      val picturesPath = configFile.exists match {
        case true => getPicturesPathFromConfigFile
        case false => "." + File.separator
      }
      picturesCacheManager.load(picturesPath)
    }
  }

  protected def getPicturesPathFromConfigFile: String = {
    val picturesPath = config.getString("pictures_path", ".")
    if (!picturesPath.endsWith(File.separator)) {
      picturesPath + File.separator
    } else {
      picturesPath
    }
  }

}
