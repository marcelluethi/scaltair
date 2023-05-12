package scalismo.plot.plots.examples

import scalismo.plot.Chart
import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
import scalismo.plot.Channel
import scalismo.plot.Scale
import scalismo.plot.DataValue
import scalismo.plot.ChartProperties

/** Example charts, which show how to use the vega lite specification directly.
  * This is useful if you want maximum control over the plot.
  */
object SimpleCharts:

  def barChart(): Unit =
    val data = Map(
      "x" -> Seq("A", "B", "C", "D", "E").map(DataValue.Nominal(_)),
      "y" -> Seq(5, 3, 6, 7, 2).map(DataValue.Quantitative(_))
    )
    Chart(data)
      .encode(Channel.X("x"), Channel.Y("y"))
      .markBar()    
      .show()

  def scatterPlot(): Unit =
    val data = Map(
      "x" -> Seq(1, 2, 3, 4, 5).map(DataValue.Quantitative(_)),
      "y" -> Seq(5, 3, 6, 7, 2).map(DataValue.Quantitative(_))
    )

    Chart(data)
      .encode(Channel.X("x"), Channel.Y("y"))
      .markPoint()
      .show()

  def linePlot(): Unit =
    val xs = Seq.range(0, 200).map(_ / 10.0)
    val ys = xs.map(x => math.sin(x) + math.cos(x))
    val data = Map(
      "x" -> xs.map(DataValue.Quantitative(_)),
      "y" -> ys.map(DataValue.Quantitative(_))
    )

    Chart(data)
      .encode(Channel.X("x"), Channel.Y("y"))
      .markLine()
      .properties(ChartProperties(title = "line", titleFontSize = 20))
      .show()

  def lineSeries(): Unit =
    val xs = Seq.range(0, 200).map(_ / 10.0)
    val ys = xs.map(x => math.sin(x) + math.cos(x))
    val zs = xs.map(x => math.sin(x) - math.cos(x))

    val data = Map(
      "x" -> (xs ++ xs).map(DataValue.Quantitative(_)),
      "y" -> (ys.map(DataValue.Quantitative(_))
        ++
          zs.map(DataValue.Quantitative(_))),
      "series" -> (Seq.fill(xs.length)(DataValue.Nominal("sin"))
        ++
          Seq.fill(xs.length)(DataValue.Nominal("cos")))
    )
    Chart(data)
      .encode(Channel.X("x"), Channel.Y("y"), Channel.Color("series"))
      .markLine()
      .show()

  def histogram(): Unit =
    val xs = Seq.fill(1000)(scala.util.Random.nextGaussian())

    val data = Map(
      "x" -> xs.map(DataValue.Quantitative(_))
    )

    Chart(data)
      .encode(Channel.X("x").binned(), Channel.Y("x").count())
      .markBar()
      .show()

  def bubblePlot(): Unit =

    val xs = Seq.range(-10, 10)
    val data = Map(
      "x" -> xs.map(DataValue.Quantitative(_)),
      "y" -> xs.map(x => x * x).map(DataValue.Quantitative(_)),
      "size" -> Seq.range(0, xs.length).map(DataValue.Quantitative(_)),
      "color" -> xs
        .map(x => if x < 0 then 1 else 2)
        .map(DataValue.Quantitative(_))
    )

    Chart(data)
      .encode(
        Channel.X("x").binned(),
        Channel.Y("y"),
        Channel.Size("size"),
        Channel.Color("color")
      )
      .markCircle()
      .show()

  @main def runSimpleCharts() =
    barChart()
    scatterPlot()
    linePlot()
    lineSeries()
    histogram()
    bubblePlot()
