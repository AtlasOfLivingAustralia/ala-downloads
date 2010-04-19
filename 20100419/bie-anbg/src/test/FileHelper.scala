package test
import java.io._

class FileHelper(file : File) {
  
  def write(text : String) : Unit = {
    val fw = new FileWriter(file)
    try{ fw.write(text) }
    finally{ fw.close }
  }
  
  def foreachLine(proc : String=>Unit) : Unit = {
    val br = new BufferedReader(new FileReader(file))
    try{ 
      while(br.ready){ 
        proc(br.readLine)
      }
    }
    finally{ br.close }
  }
  
  def deleteAll : Unit = {
    def deleteFile(dfile : File) : Unit = {
      if(dfile.isDirectory){
        val subfiles = dfile.listFiles
        if(subfiles != null)
          subfiles.foreach{ f => deleteFile(f) }
      }
      dfile.delete
    }
    deleteFile(file)
  }
}

/* Define a extensions to java.io.File
 */
object FileHelper{
  implicit def file2helper(file : File) = new FileHelper(file)
}
