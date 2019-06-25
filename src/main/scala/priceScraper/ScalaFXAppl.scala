package priceScraper

import java.io.File

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.ReadOnlyObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.{Cursor, Scene}
import scalafx.scene.control.TabPane.TabClosingPolicy
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, HBox, Pane, Priority, VBox}
import spray.json._
import spray.json.DefaultJsonProtocol._

//import Watcher.WatcherJsonFormat

object ScalaFXAppl extends JFXApp {
  private val websitesFilePath = "res/websites.json"
  private val watchlistFilePath = "res/watchlist.json"

  var shops: List[Shop] = FileManager.load(websitesFilePath).convertTo[List[Shop]]
//  var watchList: List[Watcher] = FileManager.load(watchlistFilePath).convertTo[List[Watcher]]
  var searchResults: List[SearchResult] = Nil

  var observableWatchList: ObservableBuffer[Watcher] = ObservableBuffer(FileManager.load(watchlistFilePath).convertTo[List[Watcher]])

  private val searchTab = new Tab {
    text = "Search"
    content = new HBox {
      hgrow = Priority.Always
      children = List(
        new VBox {
          prefWidth = 200
          style = "-fx-border-color: gray"
          children = shops.map(_.checkBox)
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

//    val watchlistSidePane = new VBox {
//      prefWidth = 200
//      style = "-fx-border-color: gray"
//      children = observableWatchList.map(_.checkBox)
//    }

    val mainPane = new Pane()

    private val listViewSidepanel = new ListView[Watcher] {
      prefWidth = 200
      style = "-fx-border-color: gray"
      cellFactory = Watcher.listViewCellFactory
      val selectedItem = new ReadOnlyObjectProperty[Watcher](selectionModel().selectedItemProperty())
      selectedItem.onChange((_, _, watcher) => {
        if(watcher == null)
          mainPane.children.clear()
        else
          mainPane.children = List(watcher.toScalaFX)
      })
      items = observableWatchList
    }

//    listViewSidepanel.selectionModel().selectFirst()

    content = new HBox {
      hgrow = Priority.Always
      children = List(
//        watchlistSidePane
        listViewSidepanel,
        mainPane
      )
    }

//    content = new BorderPane()
  }

  stage = new PrimaryStage {
    title = "PriceScraper"
    width = 800
    height = 600

    scene = new Scene {
      println(new File(".").getAbsolutePath)
      stylesheets = List("/css/priceScraper.css")
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

  override def stopApp(): Unit = {
    FileManager.save(observableWatchList.toList.toJson, "watchlist.json")
  }
}
