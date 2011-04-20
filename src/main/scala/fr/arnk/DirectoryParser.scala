package fr.arnk

import tools.nsc.io.{File, Directory}

class DirectoryParser(rootPath: String) {

  def listPictures: Map[String, List[String]] = {
    def parseDirectory(directory: Directory, parentDirectoryName: String): Map[String, List[String]] = {
      def addPicture(list: List[String], f: File): List[String] = {
        f.extension match {
          case e if e.toLowerCase.endsWith("png") || e.toLowerCase.endsWith("jpg") => f.name :: list
          case e => list
        }
      }

      val pictures = directory.files.foldLeft(List[String]())(addPicture)
      val fullDirectoryPath = parentDirectoryName + directory.name + '/'
      directory.dirs.foldLeft(Map(fullDirectoryPath -> pictures))((m, dir) => m ++ parseDirectory(dir, fullDirectoryPath))
    }

    val rootDirectory = Directory(rootPath)
    return rootDirectory.dirs.foldLeft(Map[String, List[String]]())((m, dir) => m ++ parseDirectory(dir, ""))
  }

}

object DirectoryParser {
  def apply(rootPath: String) = {
    new DirectoryParser(rootPath);
  }
}
