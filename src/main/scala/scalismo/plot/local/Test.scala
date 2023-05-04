package scalismo.plot.local

import scalismo.plot.data.DataFrame
import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
import scalismo.plot.data.DataFrame.CellValue

object Test:

  def main(args: Array[String]): Unit =
    val xs = Seq(1.0, 2.0, 3.0, 4.0)
    val df = DataFrame(
      Seq(
        DataFrame.Column.ofContinuous(xs, "x"),
        DataFrame.Column.ofContinuous(xs.map(x => x * x), "x squared"),
        DataFrame.Column.ofContinuous(xs.map(x => Math.sin(x)), "sin(x)")
      )
    )

    val boneData = DataFrame.fromCSV(java.io.File("data.csv")).get
    val xValues = Seq(1.0, 2.0, 3.0, 4.0)
    val dfWithError = DataFrame(
      Seq(
        DataFrame.Column.ofContinuous(xValues, "x"),
        DataFrame.Column.ofContinuous(xValues.map(x => x * x), "x squared"),
        DataFrame.Column.ofContinuous(xValues.map(x => x * x - 5), "lower"),
        DataFrame.Column.ofContinuous(xValues.map(x => x * x + 5), "upper")
      )
    )
    dfWithError.plot
      .linePlotWithErrorBand(
        x = "x",
        y = "x squared",
        lowerBand = "lower",
        upperBand = "upper",
        title = " A line plot with error bars"
      )
      .show()
