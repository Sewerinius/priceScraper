package priceScraper

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import scalafx.beans.binding.Bindings._
import scalafx.beans.property.{BooleanProperty, ObjectProperty}
import scalafx.scene.Cursor
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, VBox}
import scalafx.scene.paint.Color
import spray.json.DefaultJsonProtocol._
import spray.json._

case class Watcher private (name: String, shop: Shop, webPage: String, interval: ObjectProperty[Int], active: BooleanProperty) {
  def this(name: String, shop: Shop, webPage: String, interval: Int) = {
    this(name, shop, webPage, ObjectProperty(interval), BooleanProperty(true))
    scrapData()
  }

  def this(name: String, shop: Shop, webPage: String, interval: Int, active: Boolean) = {
    this(name, shop, webPage, ObjectProperty(interval), BooleanProperty(active))
  }

  val cellText: String = shop.name + " " + name

  val fileString: String = webPage.replaceAll("[ <>:\"\\\\|\\?\\*/\\.]", "_")

  lazy val price: Double = watchpoints.last._2

  def scrapData(): Double = {
    val browser = JsoupBrowser()
    val doc = browser.get(webPage)
    val timestamp = System.currentTimeMillis()/1000
    val price = (doc >> shop.attributes("price")).toDouble
    FileManager.appendWatchpoint(this, timestamp, price)
    price
  }

  lazy val watchpoints: List[(Long, Double)] = FileManager.loadWatchpoints(this)

  lazy val toScalaFX = new VBox {
    children = List(
      new CheckBox() {
        text = name
        textFill <== when(active) choose Color.Black otherwise Color.Gray
        selected <==> active
      },
      new HBox() {
        children = List(
          new Spinner[Int](1, Int.MaxValue, interval.value) {
            interval <== this.value
            editable = true
//            dela
//            re
//            println(dela)
          },
          new Button("Open website") {
            onAction = _ => {
              ScalaFXAppl.hostServices.showDocument(webPage)
            }
          }
        )
      },
      ChartBuilder.chart(watchpoints, "Czas[s] (od 1970)", "Cena")
    )
  }
}

object Watcher {
//  val watchList: List[Watcher] = FileManager.load("res/watchlist.json").convertTo[List[Watcher]]//load()
//  val t = type priceScraper.Watcher

  def apply(name: String, shop: Shop, webPage: String, interval: Int) = new Watcher(name, shop, webPage, interval)
  def apply(name: String, shop: Shop, webPage: String, interval: Int, active: Boolean) = new Watcher(name, shop, webPage, interval, active)

  implicit object WatcherJsonFormat extends RootJsonFormat[Watcher] {
    override def write(watcher: Watcher) = JsObject(
      "name" -> watcher.name.toJson,
      "shop" -> watcher.shop.name.toJson,
      "webpage" -> watcher.webPage.toJson,
      "interval" -> watcher.interval.value.toJson,
      "active" -> watcher.active.value.toJson
    )

    @throws(classOf[IllegalArgumentException])
    @throws(classOf[NoSuchElementException])
    override def read(json: JsValue): Watcher = {
      json.asJsObject.getFields("shop", "webpage", "interval", "active", "name") match {
        case Seq(shopName, webpage, interval, active, name) =>
          val shop = Shop.loadedShops.find(_.name == shopName.convertTo[String]).getOrElse(deserializationError("Cannot deserialize priceScraper.Watcher: unknown shop. Shop: " + shopName.convertTo[String]))
          Watcher(name.convertTo[String], shop, webpage.convertTo[String], interval.convertTo[Int], active.convertTo[Boolean])
        case other => deserializationError("Cannot deserialize priceScraper.Watcher: invalid input. Raw:" + other)
      }
    }
  }

  def listViewCellFactory(lv: ListView[Watcher]) = new ListCell[Watcher] {
    this.cursor = Cursor.Hand
    item.onChange((_, _, watcher) => {
      lv.selectionModel().selectFirst()
      if(watcher == null)
        this.text = ""
      else {
        this.textFill <== when(watcher.active) choose Color.Black otherwise Color.Gray
        this.text = watcher.cellText
      }
    })
  }
}