package fr.arnk

import tools.nsc.io.File

object Configuration {

  val ConfigFileName = "gdrawing.config"
  val CacheFileName = "gdrawing.cache"
  val ConfigFolderName = ".gdrawing"

  val ConfigFolderPath = System.getProperty("user.home") + File.separator + ConfigFolderName
  val ConfigFilePath = ConfigFolderPath + File.separator + ConfigFileName
  val CacheFilePath = ConfigFolderPath + File.separator + CacheFileName

}
