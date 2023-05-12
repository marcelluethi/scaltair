package scalismo.plot.plots.examples

import scalismo.plot.Chart
import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
import scalismo.plot.ChartProperties
import scalismo.plot.Channel
import scalismo.plot.Scale
import scalismo.plot.ChartWithViews
import scalismo.plot.DataValue

object CompositeCharts:

  val data = Map(
    "x" -> Seq("A", "B", "C", "D", "E").map(DataValue.Nominal(_)),
    "y" -> Seq(5, 3, 6, 7, 2).map(DataValue.Quantitative(_)),
    "y2" -> Seq(6, 4, 7, 8, 3).map(x => DataValue.Nominal(x.toString))
  )

  val view1 = Chart(data)
    .encode(Channel.X("x"), Channel.Y("y"))
    .markLine()

  val view2 = Chart(data)
    .encode(Channel.X("x"), Channel.Y("y"), Channel.Size("y"))
    .markPoint()

  def layeredChart(): Unit =
    view1
      .overlay(view2)
      .show()

  def stackedChart(): Unit =

    val verticalCharts = (view1
      .vConcat(view2))
      .hConcat(view1.vConcat(view2))
      .properties(ChartProperties(title = "Vertical charts"))
      .show()


  def chartWithTwoAxis() : Unit = 
    val base = Chart(data).encode(Channel.X("x"), Channel.Y("y")).markArea()
    val line = Chart(data).encode(Channel.X("x"), Channel.Y("y2")).markPoint()

    base.overlay(line).show()


  @main def runCompositeCharts(): Unit =
    layeredChart()
    stackedChart()
    chartWithTwoAxis()
