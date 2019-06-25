package priceScraper

import java.io.{BufferedWriter, FileWriter}
import java.text.SimpleDateFormat
import java.util.Date

import spray.json._

import scala.io.Source

object FileManager {
  val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def load(filePath: String): JsValue = {
    val sourceFile = Source.fromFile(filePath)
    val value = sourceFile.mkString.parseJson
    sourceFile.close()
    value
  }

//  def load[A <: Loadable[A]](filePath: String): List[A] = {
//    val watches = Source.fromFile(filePath)
//    val shops = watches.mkString.parseJson.convertTo[List[A]]
//    watches.close()
//    shops
//  }

  def save[T](obj: JsValue, filePath: String): Unit = {
    val writer = new BufferedWriter(new FileWriter("res/watchlist.json"))
    writer.write(obj.prettyPrint)
    writer.close()
  }

  def loadWatchpoints(watcher: Watcher): List[(Long, Double)] = {
    val sourceFile = Source.fromFile("watchData/"+watcher.fileString+".csv")
    val res = sourceFile.getLines().map(_.split(",") match {
      case Array(a:String, b:String, _*) => (dateFormat.parse(a).getTime/1000, b.toDouble)
    }).toList
    sourceFile.close()
    res
  }

  def appendWatchpoint(watcher: Watcher, timeInS: Long, price: Double): Unit = {
    val writer = new BufferedWriter(new FileWriter("watchData/"+watcher.fileString+".csv", true))
    writer.write(dateFormat.format(new Date(timeInS * 1000)) + "," + price)
    writer.newLine()
    writer.close()
  }
}
