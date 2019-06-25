package priceScraper

import spray.json.DefaultJsonProtocol._

import scala.collection.mutable.PriorityQueue

object ScraperAppl {
  def main(args: Array[String]): Unit = {
    val watches = FileManager.load("res/watchlist.json").convertTo[List[Watcher]].filter(_.active.value)
//    val ordering = Ordering.by[(Long, Watcher), Long](_._1).reverse
    val queue = PriorityQueue[(Long, Watcher)](watches.map(w => (w.watchpoints.last._1 + w.interval.value * 60, w)):_*)(Ordering.by[(Long, Watcher), Long](_._1).reverse)
    while (true) {
      val (time, watcher) = queue.dequeue()
      println(watcher.name)
      var currTime = System.currentTimeMillis()
      if (time * 1000 > currTime) {
        printf("Sleeping for %d min\n", (time - currTime/1000)/60)
        Thread.sleep(time * 1000 - currTime)
      }

      printf("Price: %f\n", watcher.scrapData())
      currTime = System.currentTimeMillis()
      queue.enqueue((currTime/1000 + watcher.interval.value * 60, watcher))
    }
  }
}
