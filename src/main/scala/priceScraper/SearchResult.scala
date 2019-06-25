package priceScraper

import java.net.{URI, URLEncoder}

import scalafx.scene.control.{Button, Label, ListView}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout.{HBox, VBox}

case class SearchResult(name: String, price: Double, imgUrl: String, address: String, shop: Shop) {
  //  outer =>
//  val watcher =

  val toScalaFX: HBox = new HBox {
    //    var outerRef: SearchResult = outer

    children = List(
      new ImageView {
        image = new Image(imgUrl.replaceAll(" ", "%20"), 128, 128, true, true, true)
      },
      new VBox() {
        children = List(
          new Label(name),
          new HBox() {
            children = List(
              new VBox() {
                children = List(
                  new Label("Cena: " + price.toString),
                  new Label("Sklep: " + shop.name)
                )
              },
              new Button("Open website") {
                onAction = _ => {
                  println(address.hashCode)
                  ScalaFXAppl.hostServices.showDocument(address)
                }
              },
              new Button("Add to watchlist") {
                onAction = _ => {
                  ScalaFXAppl.observableWatchList.prepend(Watcher(name, shop, address, 1440))
                }
              }
            )
          }
        )
      }
    )
  }
}
