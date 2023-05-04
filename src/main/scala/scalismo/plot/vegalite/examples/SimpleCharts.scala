package scalismo.plot.vegalite.examples

import scalismo.plot.vegalite.Data.DataValue
import scalismo.plot.vegalite.{Chart, View, Encoding}
import scalismo.plot.vegalite.SingleView
import scalismo.plot.vegalite.Mark
import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
import scalismo.plot.vegalite.Title

/** Example charts, which show how to use the vega lite specification directly.
  * This is useful if you want maximum control over the plot.
  */
object SimpleCharts {

  def barChart(): Unit =
    val data = Map(
      "x" -> Seq("A", "B", "C", "D", "E").map(DataValue.Nominal(_)),
      "y" -> Seq(5, 3, 6, 7, 2).map(DataValue.Quantitative(_))
    )
    val view = SingleView(
      mark = Mark.Bar,
      encoding = Encoding(channels =
        Seq(
          Encoding.Channel.X("x", Encoding.FieldType.Nominal),
          Encoding.Channel.Y("y", Encoding.FieldType.Quantitative)
        )
      )
    )
    val chart = Chart(data, view, Title("bar"))
    chart.show()

  def scatterPlot(): Unit =
    val data = Map(
      "x" -> Seq(1, 2, 3, 4, 5).map(DataValue.Quantitative(_)),
      "y" -> Seq(5, 3, 6, 7, 2).map(DataValue.Quantitative(_))
    )
    val view = SingleView(
      mark = Mark.Point,
      encoding = Encoding(channels =
        Seq(
          Encoding.Channel.X("x", Encoding.FieldType.Quantitative),
          Encoding.Channel.Y("y", Encoding.FieldType.Quantitative)
        )
      )
    )
    val chart = Chart(data, view, Title("scatter"))
    chart.show()

  def linePlot(): Unit =
    val xs = Seq.range(0, 200).map(_ / 10.0)
    val ys = xs.map(x => math.sin(x) + math.cos(x))
    val data = Map(
      "x" -> xs.map(DataValue.Quantitative(_)),
      "y" -> ys.map(DataValue.Quantitative(_))
    )
    val view = SingleView(
      mark = Mark.Line,
      encoding = Encoding(channels =
        Seq(
          Encoding.Channel.X("x", Encoding.FieldType.Quantitative),
          Encoding.Channel.Y("y", Encoding.FieldType.Quantitative)
        )
      )
    )
    val chart = Chart(data, view, Title("line"))
    chart.show()

  def lineSeriesPlot(): Unit =
    val xs = Seq.range(0, 200).map(_ / 10.0)
    val ys = xs.map(x => math.sin(x) + math.cos(x))
    val zs = xs.map(x => math.sin(x) - math.cos(x))

    val data = Map(
      "x" -> (xs ++ xs).map(DataValue.Quantitative(_)),
      "y" -> (ys.map(DataValue.Quantitative(_)) ++ zs.map(
        DataValue.Quantitative(_)
      )),
      "series" -> (Seq.fill(xs.length)(DataValue.Nominal("sin")) ++ Seq.fill(
        xs.length
      )(DataValue.Nominal("cos")))
    )
    val view = SingleView(
      mark = Mark.Line,
      encoding = Encoding(channels =
        Seq(
          Encoding.Channel.X("x", Encoding.FieldType.Quantitative),
          Encoding.Channel.Y("y", Encoding.FieldType.Quantitative),
          Encoding.Channel.Color("series")
        )
      )
    )
    val chart = Chart(data, view, Title("line"))
    chart.show()

  def histogram(): Unit =
    val xs = Seq.fill(1000)(scala.util.Random.nextGaussian())

    val data = Map(
      "x" -> xs.map(DataValue.Quantitative(_))
    )

    val encoding = Encoding(
      Seq(
        Encoding.Channel.X(
          "x",
          Encoding.FieldType.Quantitative,
          Seq(Encoding.ChannelProp.Bin(true))
        ),
        Encoding.Channel.Y(
          "x",
          Encoding.FieldType.Quantitative,
          Seq(Encoding.ChannelProp.Aggregate(Encoding.AggregateType.Count))
        )
      )
    )

    val view = SingleView(
      mark = Mark.Bar,
      encoding = encoding
    )

    val chart = Chart(data, view, Title("line"))
    chart.show()

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

    val encoding = Encoding(
      Seq(
        Encoding.Channel.X(
          "x",
          Encoding.FieldType.Quantitative,
          Seq(Encoding.ChannelProp.Bin(true))
        ),
        Encoding.Channel.Y(
          "y",
          Encoding.FieldType.Quantitative
        ),
        Encoding.Channel.Size(
          "size"
        ),
        Encoding.Channel.Color(
          "size"
        )
      )
    )

    val view = SingleView(
      mark = Mark.Circle,
      encoding = encoding
    )

    val chart = Chart(data, view, Title("line"))
    chart.show()

  @main def runSimpleCharts() =
    barChart()
    scatterPlot()
    linePlot()
    lineSeriesPlot()
    histogram()
    bubblePlot()
}
