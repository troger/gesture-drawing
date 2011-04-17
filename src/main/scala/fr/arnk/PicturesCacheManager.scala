package fr.arnk

import java.io.FileWriter
import io.Source
import tools.nsc.io._
import com.codahale.jerkson.Json._
import Configuration._

class PicturesCacheManager {

  case class PicturesCache(picturesPath: String, pictures: Map[String, List[String]], categories: List[String])

  var picturesCache: Option[PicturesCache] = None

  def picturesCacheLoaded = {
    picturesCache.isDefined
  }

  def load(picturesPath: String) {
    if (!File(CacheFilePath).exists) {
      val fw = new FileWriter(CacheFilePath)
      try {
        val pictures = DirectoryParser(picturesPath).listPictures
        val categories = pictures.keys.foldLeft(List[String]())((list, k) => k :: list).sortBy((c) => c)

        picturesCache = Some(new PicturesCache(picturesPath, pictures, categories))
        fw.write(generate(picturesCache))
      } finally {
        fw.close()
      }
    } else {
      picturesCache = Some(parse[PicturesCache](Source.fromInputStream(File(CacheFilePath).inputStream()).mkString))
    }
  }

  def reload() {
    picturesCache = None
    val cacheFile = File(CacheFilePath)
    cacheFile.deleteIfExists()
  }

  def getPictures = {
    picturesCache match {
      case Some(pc) => Some(pc.pictures)
      case None => None
    }
  }

  def getCategories = {
    picturesCache match {
      case Some(pc) => Some(pc.categories)
      case None => None
    }
  }

  def getPicturesPath = {
    picturesCache match {
      case Some(pc) => Some(pc.picturesPath)
      case None => None
    }
  }

}
