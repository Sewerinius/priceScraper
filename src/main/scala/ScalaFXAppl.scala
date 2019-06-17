import java.io.File

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.TabPane.TabClosingPolicy
import scalafx.scene.control._
import scalafx.scene.layout.{HBox, Priority, VBox}

object ScalaFXAppl extends JFXApp {
  var shops: List[Shop] = Shop.load()
//  var searchResultBuffer = Observable
  var searchResults: List[SearchResult] = Nil
//  var watchList =

  private val searchTab = new Tab {
    text = "Search"
    content = new HBox {
      hgrow = Priority.Always
      children = List(
        new VBox {
          prefWidth = 200
          style = "-fx-border-color: gray"
          children = shops.map(_.toCheckBox)
        },
        new VBox {
          hgrow = Priority.Always

          private val searchResultsContainer = new VBox ()

          children = List(
            new ToolBar() {
              private val searchField = new TextField {
                hgrow = Priority.Always
              }

              content = List(
                searchField,
                new Button("Search") {
                  defaultButton = true
                  onAction = _ => {
                    val s = Shop.parseToSearch(searchField.text.value)
                    searchResults = shops.filter(_.active.value).flatMap(_.search(s))
                    searchResultsContainer.children = searchResults.map(_.toScalaFX)
                  }
                }
              )
            },
            new ScrollPane {
              vgrow = Priority.Always
              content = searchResultsContainer
            }
          )
        }
      )
    }
  }

  private val watchTab = new Tab {
    text = "Watch"
  }

  stage = new PrimaryStage {
    title = "PriceScraper"
    width = 800
    height = 600

    scene = new Scene {
      println(new File(".").getAbsolutePath)
      stylesheets = List("res/css/priceScraper.css")
      root = new TabPane {
        tabClosingPolicy = TabClosingPolicy.Unavailable
        tabs = List(
          searchTab,
          watchTab
        )

        tabMinWidth <== width / tabs.size() - 23
        tabMaxWidth <== width / tabs.size() - 23
      }
    }
  }
}