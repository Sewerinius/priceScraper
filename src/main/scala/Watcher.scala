import scala.io.Source
import spray.json.DefaultJsonProtocol._
import spray.json._

case class Watcher(shop: Shop, webPage: String, interval: Int, active: Boolean) {

}

//object Watcher {
////  implicit val watcherFormat = jsonFormat4(Watcher.apply)
//
//  def load(shops: List[Shop]): Unit = {
//    val watches = Source.fromFile("res/watches.json")
//    val shops = watches.mkString.parseJson.convertTo[List[Watcher]]
//    watches.close()
//    shops
//  }
//
//  implicit object WatcherJsonFormat extends RootJsonFormat[Watcher] {
//    override def write(obj: Watcher): JsValue = obj.toJson
//
//    @throws(classOf[IllegalArgumentException])
//    override def read(json: JsValue): Watcher = {
//      json.asJsObject.fields("shop") match {
//        case JsString(shopName) =>
//        case _ => throw new IllegalArgumentException
//      }
//    }
//  }
//}