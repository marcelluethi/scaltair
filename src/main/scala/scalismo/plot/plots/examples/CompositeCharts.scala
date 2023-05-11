package scalismo.plot.plots.examples

import scalismo.plot.plots.Plot
import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
import scalismo.plot.plots.{Channel}
import scalismo.plot.plots.Scale
import scalismo.plot.plots.PlotWithViews
import scalismo.plot.DataValue

object CompositeCharts:

  val data = Map(
    "x" -> Seq("A", "B", "C", "D", "E").map(DataValue.Nominal(_)),
    "y" -> Seq(5, 3, 6, 7, 2).map(DataValue.Quantitative(_))
  )

  val view1 = Plot(data)
    .encode(Channel.X("x"), Channel.Y("y"))
    .line()

  val view2 = Plot(data)
    .encode(Channel.X("x"), Channel.Y("y"), Channel.Size("y"))
    .point()

  def layeredChart(): Unit =
    view1
      .overlay(view2)
      .chart(title = "Bar Chart")
      .show()

  def stackedChart(): Unit =

    val verticalCharts = (view1
      .vConcat(view2))
      .hConcat(view1.vConcat(view2))
      .chart(title = "stacked")
      .show()

  @main def runCompositeCharts(): Unit =
    layeredChart()
    stackedChart()
