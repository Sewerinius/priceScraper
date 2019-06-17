import spray.json._

import scala.io.Source

object SprayAppl {
  def main(args: Array[String]): Unit = {
//    println(
//      """[{
//        "name": "X-Kom"
//        }]""".parseJson)
    println(Shop.load().map(_.toString).mkString)
  }
}
