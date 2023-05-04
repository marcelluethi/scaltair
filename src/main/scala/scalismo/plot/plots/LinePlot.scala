// package scalismo.plot.plots

// import scalismo.plot.vegalite.Data.DataValue
// import scalismo.plot.vegalite.{Chart, View, Encoding}
// import scalismo.plot.vegalite.SingleView
// import scalismo.plot.vegalite.Mark
// import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
// import scalismo.plot.vegalite.TitleProp
// import scalismo.plot.vegalite.Title
// import scalismo.plot.plots.LinePlot

// case class LinePlot(
//     xs: Seq[Double] = Seq.empty,
//     ys: Seq[Double] = Seq.empty,
//     xLabel: String = "",
//     yLabel: String = "",
//     title: String = "",
//     fontSize: Int = 20
// ):

//   def x(xs: Seq[Double], xLabel : String = ""): LinePlot = copy(xs = xs, xLabel = xLabel)
//   def y(ys: Seq[Double], yLabel : String = "" ): LinePlot = copy(ys = ys, yLabel = yLabel)
//   def fontSize(fontSize: Int): LinePlot = copy(fontSize = fontSize)
//   def title(title: String): LinePlot = copy(title = title)

//   def chart: Chart =
//     val data = Map(
//       "x" -> xs.map(DataValue.Quantitative(_)),
//       "y" -> ys.map(DataValue.Quantitative(_))
//     )

//     import Encoding.AxisProp.*
//     import Encoding.ChannelProp.*

//     val view = SingleView(
//       mark = Mark.Line,
//       encoding = Encoding(channels =
//         Seq(
//           Encoding.Channel.X(
//             "x",
//             Encoding.FieldType.Quantitative,
//             Seq(Axis(Seq(LabelFontSize(fontSize), TitleFontSize(fontSize))))
//           ),
//           Encoding.Channel.Y(
//             "y",
//             Encoding.FieldType.Quantitative,
//             Seq(Axis(Seq(LabelFontSize(fontSize), TitleFontSize(fontSize))))
//           )
//         )
//       )
//     )

//     Chart(
//       data,
//       view,
//       Title(title, Seq(TitleProp.FontSize((fontSize * 1.5).toInt)))
//     )

// @main def runFormattingPlots() =
//   val xs = Seq.range(0, 200).map(_ / 10.0)
//   LinePlot()
//     .x(xs, "x")
//     .y(xs.map(x => math.sin(x) + math.cos(x)), "y")
//     .fontSize(20)
//     .title("a line plot")
//     .chart
//     .show()
