package otherpackage

import scaltair.*
import scaltair.PlotTargetBrowser.given
import scaltair.vegalite.datasets.VegaDatasets
import scaltair.Data.ColumnData

@main def examplePlot() =
  def g(x: Double): Double = Math.exp(-(x * x) / 2.0) / Math.sqrt(2 * Math.PI)
  val xs = (-500 to 500).map(_ / 100.0)
  val ys = xs.map(g)
  val zs = xs.map(x => g(x + 2))

  val data = Map(
    "x" -> (xs ++ xs),
    "y" -> (ys ++ zs),
    "series" -> (Seq.fill(xs.length)("ys") ++ Seq.fill(xs.length)("zs"))
  )
  Chart(data)
    .encode(
      Channel
        .X("x", FieldType.Quantitative)
        .scale(Scale().domain(Domain(-10, 10))),
      Channel.Y("y", FieldType.Quantitative),
      Channel.Color("series", FieldType.Nominal)
    )
    .mark(Mark.Area().opacity(0.3))
    .show()
