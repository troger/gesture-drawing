package fr.arnk

import tools.nsc.io.File
import net.lag.configgy.{Configgy, Config}

object Configuration {

  val ConfigFileName = "gdrawing.conf"
  val CacheFileName = "gdrawing.cache"
  val ConfigFolderName = ".gdrawing"

  val ConfigFolderPath = System.getProperty("user.home") + File.separator + ConfigFolderName
  val ConfigFilePath = ConfigFolderPath + File.separator + ConfigFileName
  val CacheFilePath = ConfigFolderPath + File.separator + CacheFileName

  private var _config: Option[Config] = None
  def config = _config.get

  def configure() {
    if(_config.isEmpty && configFileExists) {
      println("configgured!")
      Configgy.configure(ConfigFilePath)
      _config = Some(Configgy.config)
    }
    _config
  }

  def configFileExists: Boolean = {
    val configFile = File(ConfigFilePath)
    configFile.exists
  }

}
