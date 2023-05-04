package scalismo.plot.plots

import scalismo.plot.vegalite.Data.DataValue
import scalismo.plot.vegalite.Mark
import scalismo.plot.vegalite.Encoding
import scalismo.plot.vegalite.{View, LayeredView}
import scalismo.plot.vegalite.Chart
import scalismo.plot.vegalite.Title
import scalismo.plot.vegalite.TitleProp

import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
import scalismo.plot.vegalite.SingleView
import scalismo.plot.vegalite.HConcatViews
import scalismo.plot.vegalite.CompositeView
import scalismo.plot.vegalite.VConcatViews

case class Plot(
    data: Map[String, Seq[DataValue]]
):

  def encode(channels: Channel*): PlotWithEncoding =

    val vegaChannels = for channel <- channels yield channel match
      case Channel.X(fieldName, binned, scale, axis) =>
        val xFieldType = data(fieldName).head match
          case _: DataValue.Quantitative => Encoding.FieldType.Quantitative
          case _: DataValue.Nominal      => Encoding.FieldType.Nominal

        val scaleProp = scale.map(s =>
          Encoding.ChannelProp.Scale(
            Encoding.ScaleSpec.IncludeZero(s.axisIncludesZero)
          )
        )

        val axisProp = axis.map(a =>
          Encoding.ChannelProp.Axis(
            Seq(
              Encoding.AxisProp.LabelFontSize(a.labelFontSize),
              Encoding.AxisProp.TitleFontSize(a.titleFontSize)
            )
          )
        )
        Encoding.Channel
          .X(
            fieldName,
            xFieldType,
            Seq(
              Encoding.ChannelProp.Bin(binned)
            ) ++ scaleProp.toSeq ++ axisProp.toSeq
          )
      case Channel.Y(fieldName, agg, scale, axis) =>
        val yFieldType = data(fieldName).head match
          case _: DataValue.Quantitative => Encoding.FieldType.Quantitative
          case _: DataValue.Nominal      => Encoding.FieldType.Nominal

        val aggProp = agg.map(a => Encoding.ChannelProp.Aggregate(a))
        val scaleProp = scale.map(s =>
          Encoding.ChannelProp.Scale(
            Encoding.ScaleSpec.IncludeZero(s.axisIncludesZero)
          )
        )
        val axisProp = axis.map(a =>
          Encoding.ChannelProp.Axis(
            Seq(
              Encoding.AxisProp.LabelFontSize(a.labelFontSize),
              Encoding.AxisProp.TitleFontSize(a.titleFontSize)
            )
          )
        )
        Encoding.Channel.Y(
          fieldName,
          yFieldType,
          aggProp.toSeq ++ scaleProp.toSeq ++ axisProp.toSeq
        )

      case Channel.Y2(fieldName, agg, scale, axis) =>
        val yFieldType = data(fieldName).head match
          case _: DataValue.Quantitative => Encoding.FieldType.Quantitative
          case _: DataValue.Nominal      => Encoding.FieldType.Nominal

        val aggProp = agg.map(a => Encoding.ChannelProp.Aggregate(a))
        val scaleProp = scale.map(s =>
          Encoding.ChannelProp.Scale(
            Encoding.ScaleSpec.IncludeZero(s.axisIncludesZero)
          )
        )
        val axisProp = axis.map(a =>
          Encoding.ChannelProp.Axis(
            Seq(
              Encoding.AxisProp.LabelFontSize(a.labelFontSize),
              Encoding.AxisProp.TitleFontSize(a.titleFontSize)
            )
          )
        )
        Encoding.Channel.Y2(
          fieldName,
          yFieldType,
          aggProp.toSeq ++ scaleProp.toSeq ++ axisProp.toSeq
        )

      case Channel.Color(fieldName) =>
        Encoding.Channel.Color(fieldName, Seq.empty)
      case Channel.Size(fieldName) =>
        Encoding.Channel.Size(fieldName, Seq.empty)
    PlotWithEncoding(data, encoding = Encoding(vegaChannels))

case class PlotWithEncoding(
    data: Map[String, Seq[DataValue]],
    encoding: Encoding = Encoding(Seq.empty)
):

  def line(): PlotWithViews =
    PlotWithViews(data, LayeredView(Seq(SingleView(Mark.Line, encoding))))
  def circle(): PlotWithViews =
    PlotWithViews(data, LayeredView(Seq(SingleView(Mark.Circle, encoding))))
  def point(): PlotWithViews =
    PlotWithViews(data, LayeredView(Seq(SingleView(Mark.Point, encoding))))
  def bar(): PlotWithViews =
    PlotWithViews(data, LayeredView(Seq(SingleView(Mark.Bar, encoding))))
  def area(): PlotWithViews =
    PlotWithViews(data, LayeredView(Seq(SingleView(Mark.Area, encoding))))
  def boxplot(): PlotWithViews =
    PlotWithViews(data, LayeredView(Seq(SingleView(Mark.Boxplot, encoding))))
  def errorBand(): PlotWithViews =
    PlotWithViews(data, LayeredView(Seq(SingleView(Mark.ErrorBand, encoding))))

trait CompletePlot:
  def data: Map[String, Seq[DataValue]]
  def view: View
  def chart(
      title: String = "",
      titleFontSize: Int = 20,
      width: Int = 600,
      height: Int = 600
  ): Chart =
    Chart(
      data,
      view,
      Title(title, Seq(TitleProp.FontSize(titleFontSize))),
      width,
      height
    )

  def hConcat(other: CompletePlot): PlotWithCompositeView =
    PlotWithCompositeView(data, HConcatViews(Seq(this.view, other.view)))

  def vConcat(other: CompletePlot): PlotWithCompositeView =
    PlotWithCompositeView(data, VConcatViews(Seq(this.view, other.view)))

case class PlotWithViews(
    data: Map[String, Seq[DataValue]],
    view: LayeredView
) extends CompletePlot:

  def overlay(other: PlotWithViews): PlotWithViews =
    require(other.data == data, "Data must be the same")
    PlotWithViews(data, LayeredView(this.view.views ++ other.view.views))

case class PlotWithCompositeView(
    data: Map[String, Seq[DataValue]],
    view: CompositeView
) extends CompletePlot

sealed trait Channel(
    fieldName: String
)

object Channel:
  case class X(
      fieldName: String,
      isBinned: Boolean = false,
      scale: Option[Scale] = Some(Scale(axisIncludesZero = false)),
      axis: Option[Axis] = None
  ) extends Channel(fieldName):

    def scale(scale: Scale): Channel.X =
      Channel.X(fieldName, isBinned, Some(scale), axis)

    def axis(axis: Axis): Channel.X =
      Channel.X(fieldName, isBinned, scale, Some(axis))

    def binned(): Channel =
      Channel.X(fieldName, true, scale, axis)

  case class Y(
      fieldName: String,
      agg: Option[Encoding.AggregateType] = None,
      scale: Option[Scale] = Some(Scale(axisIncludesZero = false)),
      axis: Option[Axis] = None
  ) extends Channel(fieldName):

    def count(): Channel =
      Channel.Y(fieldName, Some(Encoding.AggregateType.Count), scale)

    def scale(scale: Scale): Channel.Y =
      Channel.Y(fieldName, agg, Some(scale))

    def axis(axis: Axis): Channel.Y =
      Channel.Y(fieldName, agg, scale, Some(axis))

  case class Y2(
      fieldName: String,
      agg: Option[Encoding.AggregateType] = None,
      scale: Option[Scale] = Some(Scale(axisIncludesZero = false)),
      axis: Option[Axis] = None
  ) extends Channel(fieldName):

    def count(): Channel =
      Channel.Y2(fieldName, Some(Encoding.AggregateType.Count), scale)

    def scale(scale: Scale): Channel.Y2 =
      Channel.Y2(fieldName, agg, Some(scale), axis)

    def axis(axis: Axis): Channel.Y2 =
      Channel.Y2(fieldName, agg, scale, Some(axis))

  case class Color(fieldName: String) extends Channel(fieldName)
  case class Size(fieldName: String) extends Channel(fieldName)

case class Scale(axisIncludesZero: Boolean = false):
  def includeZero(): Scale = copy(axisIncludesZero = true)

case class Axis(labelFontSize: Int = 14, titleFontSize: Int = 14):
  def labelFontSize(size: Int): Axis = copy(labelFontSize = size)
  def titleFontSize(size: Int): Axis = copy(titleFontSize = size)

object Scale:
  def includeZero(b: Boolean): Scale = Scale(false)

@main def plotSpec(): Unit =
  val xs = Seq.range(0, 200).map(_ / 10.0)
  val ys = xs.map(x => math.sin(x) + math.cos(x))
  val data = Map(
    "x" -> xs.map(DataValue.Quantitative(_)),
    "y" -> ys.map(DataValue.Quantitative(_))
  )
  Plot(data)
    .encode(
      Channel.X("x").axis(Axis(labelFontSize = 10, titleFontSize = 10)),
      Channel.Y("y"),
      Channel.Y2("y")
    )
    .circle()
    .chart(width = 600, height = 600, title = "abc", titleFontSize = 60)
    .show()
