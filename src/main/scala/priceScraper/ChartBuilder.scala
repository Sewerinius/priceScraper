package priceScraper

import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}

object ChartBuilder {
  def chart(xy: Seq[(Long, Double)], xLabel: String, yLabel: String) = {
    val xMin = xy.map(_._1).min// * 9 / 10
    val xMax = xy.map(_._1).max// * 10 / 9
    val xAxis = NumberAxis(xLabel, xMin, xMax, (xMax-xMin) / 10)
    val yMin = xy.map(_._2).min * 9 / 10
    val yMax = xy.map(_._2).max * 10 / 9
    val yAxis = NumberAxis(yLabel, yMin, yMax, (yMax-yMin) / 10)

    val toChartData = (xy: (Long, Double)) => XYChart.Data[Number, Number](xy._1, xy._2)

    val series = new XYChart.Series[Number, Number] {
      data = xy.map(toChartData)
    }

    new LineChart[Number, Number](xAxis, yAxis, ObservableBuffer(series.delegate))
  }
}
