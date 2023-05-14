// package scalismo.plot.local

// import scalismo.plot.data.DataFrame
// import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
// import scalismo.plot.data.DataFrame.CellValue
// import scalismo.plot.Chart
// import scalismo.plot.DataValue
// import scalismo.plot.Channel

// object Test:

//   def main(args: Array[String]): Unit =
//     val xs = Seq(1.0, 2.0, 3.0, 4.0)
//     val df = DataFrame(
//       Seq(
//         DataFrame.Column.ofContinuous(xs, "x"),
//         DataFrame.Column.ofContinuous(xs.map(x => x * x), "x squared"),
//         DataFrame.Column.ofContinuous(xs.map(x => Math.sin(x)), "sin(x)")
//       )
//     )

//     val boneData = DataFrame.fromCSV(java.io.File("data.csv")).get
//     val xValues = Seq(1.0, 2.0, 3.0, 4.0)
//     val dfWithError = DataFrame(
//       Seq(
//         DataFrame.Column.ofContinuous(xValues, "x"),
//         DataFrame.Column.ofContinuous(xValues.map(x => x * x), "x squared"),
//         DataFrame.Column.ofContinuous(xValues.map(x => x * x - 5), "lower"),
//         DataFrame.Column.ofContinuous(xValues.map(x => x * x + 5), "upper")
//       )
//     )

//     val data = Map(
//       "x" -> Seq("A", "B", "C", "D", "E").map(DataValue.Nominal(_)),
//       "y" -> Seq(5, 3, 6, 7, 2).map(DataValue.Quantitative(_))
//     )

//     val spec = Chart(data)
//     .encode(Channel.X("x"), Channel.Y("y"))
//     .markCircle()
//     .vegaSpec
