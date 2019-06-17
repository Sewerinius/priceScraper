import java.net.URL

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.browser.JsoupBrowser.JsoupElement
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.model.Element
import scalafx.beans.property.BooleanProperty
import scalafx.scene.control.CheckBox
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.io.Source

case class Shop(name: String, domain: String, searchURL: String, searchList: String, searchAttributes: Map[String, AttributeExtractor], tags: Array[String], attributes: Map[String, AttributeExtractor]) {
  val active = BooleanProperty(true)

  val toCheckBox: CheckBox = new CheckBox {
    text = name
    selected <==> active
  }

  def search(s : String): List[SearchResult] = {
    val browser = JsoupBrowser()
    val query = new URL(searchURL + s)
    val doc = browser.get(query.toString)
    val elements = doc >> elementList(searchList)
    elements.map(e => {
//      SearchResult(e, searchUrl, this)
      SearchResult(searchAttributes("name").extract(e), searchAttributes("price").extract(e).toInt, searchAttributes("img").extract(e), searchAttributes("address").extract(e), this)
    })
//      .map(element: JsoupElement => SearchResult(element >> searchAttributes("name"), element >> searchAttributes("price").toInt, element >> searchAttributes("name"),element >> searchAttributes("name")))
//    List()
  }
}

object Shop {
  implicit val shopFormat: RootJsonFormat[Shop] = jsonFormat[String, String, String, String, Map[String, AttributeExtractor], Array[String], Map[String, AttributeExtractor], Shop](Shop.apply, "name", "domain", "searchURL", "searchList", "searchAttributes", "tags", "attributes")

  def load(): List[Shop] = {
    val websites = Source.fromFile("res/websites.json")
    val shops = websites.mkString.parseJson.convertTo[List[Shop]]
    websites.close()
    shops
  }

  def parseToSearch(s : String): String = {
    s.split(' ').mkString("+")
  }
}