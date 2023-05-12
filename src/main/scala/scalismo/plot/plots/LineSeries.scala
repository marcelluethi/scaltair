// package scalismo.plot.plots

// import scalismo.plot.vegalite.Data.DataValue
// import scalismo.plot.vegalite.{Chart, View, Encoding}
// import scalismo.plot.vegalite.SingleView
// import scalismo.plot.vegalite.Mark
// import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
// import scalismo.plot.vegalite.TitleProp
// import scalismo.plot.vegalite.Title

// case class LineSeries(
//     seriesData: Map[String, (Seq[Double], Seq[Double])] = Map.empty,
//     title: String = "",
//     fontSize: Int = 20
// ):

//   def addSeries(name: String, xs: Seq[Double], ys: Seq[Double]): LineSeries =
//     copy(seriesData = seriesData + (name -> (xs, ys)))
//   def fontSize(fontSize: Int): LineSeries = copy(fontSize = fontSize)
//   def title(title: String): LineSeries = copy(title = title)

//   def chart: Chart =
//     val dataMaps = for (seriesName <- seriesData.keys.toSeq) yield
//       Map(
//       "x" -> seriesData(seriesName)._1.map(DataValue.Quantitative(_)),
//       "y" -> seriesData(seriesName)._2.map(DataValue.Quantitative(_)),
//       "series" -> Seq.fill(seriesData(seriesName)._1.length)(DataValue.Nominal(seriesName))
//     )

//     val data = dataMaps.reduce((acc, map) =>
//       Map("x" -> (acc("x") ++ map("x")),
//           "y" -> (acc("y") ++ map("y")),
//           "series" -> (acc("series") ++ map("series"))
//         )
//       )

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
//           ),
//           Encoding.Channel.Color(
//             "series"
//           )
//         )
//       )
//     )

//     Chart(
//       data,
//       view,
//       Title(title, Seq(TitleProp.FontSize((fontSize * 1.5).toInt)))
//     )

// @main def runtSeries() =
//   val xs = Seq.range(0, 5).map(_ / 10.0)
//   LineSeries()
//     .addSeries("sin", xs, xs.map(x => math.sin(x)))
//     .addSeries("cos", xs, xs.map(x => math.cos(x)))
//     .fontSize(20)
//     .title("a line series")
//     .chart
//     .show()
