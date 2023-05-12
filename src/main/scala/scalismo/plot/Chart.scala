package scalismo.plot

import scalismo.plot.vegalite.TitleProp
import scalismo.plot.vegalite.VegaMark
import scalismo.plot.vegalite.VegaEncoding
import scalismo.plot.vegalite.{VegaView}
import scalismo.plot.vegalite.LayeredView

import scalismo.plot.vegalite.VegaChart
import scalismo.plot.vegalite.VegaTitle
import scalismo.plot.vegalite.TitleProp

import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
import scalismo.plot.vegalite.SingleView
import scalismo.plot.vegalite.HConcatViews
import scalismo.plot.vegalite.CompositeView
import scalismo.plot.vegalite.VConcatViews
import scalismo.plot.DataValue
import scalismo.plot.plottarget.PlotTarget

case class Chart(
    data: Map[String, Seq[DataValue]]
):

  def encode(channels: Channel*): ChartWithEncoding =

    val vegaChannels = for channel <- channels yield channel match
      case Channel.X(fieldName, binned, scale, axis) =>
        val xFieldType = data(fieldName).head match
          case _: DataValue.Quantitative => VegaEncoding.FieldType.Quantitative
          case _: DataValue.Nominal      => VegaEncoding.FieldType.Nominal

        val scaleProp = scale.map(s =>
          VegaEncoding.ChannelProp.Scale(
            VegaEncoding.ScaleSpec.IncludeZero(s.axisIncludesZero)
          )
        )

        val axisProp = axis.map(a =>
          VegaEncoding.ChannelProp.Axis(
            Seq(
              VegaEncoding.AxisProp.LabelFontSize(a.labelFontSize),
              VegaEncoding.AxisProp.TitleFontSize(a.titleFontSize)
            )
          )
        )
        VegaEncoding.Channel
          .X(
            fieldName,
            xFieldType,
            Seq(
              VegaEncoding.ChannelProp.Bin(binned)
            ) ++ scaleProp.toSeq ++ axisProp.toSeq
          )
      case Channel.Y(fieldName, agg, scale, axis) =>
        val yFieldType = data(fieldName).head match
          case _: DataValue.Quantitative => VegaEncoding.FieldType.Quantitative
          case _: DataValue.Nominal      => VegaEncoding.FieldType.Nominal

        val aggProp = agg.map(a => VegaEncoding.ChannelProp.Aggregate(a))
        val scaleProp = scale.map(s =>
          VegaEncoding.ChannelProp.Scale(
            VegaEncoding.ScaleSpec.IncludeZero(s.axisIncludesZero)
          )
        )
        val axisProp = axis.map(a =>
          VegaEncoding.ChannelProp.Axis(
            Seq(
              VegaEncoding.AxisProp.LabelFontSize(a.labelFontSize),
              VegaEncoding.AxisProp.TitleFontSize(a.titleFontSize)
            )
          )
        )
        VegaEncoding.Channel.Y(
          fieldName,
          yFieldType,
          aggProp.toSeq ++ scaleProp.toSeq ++ axisProp.toSeq
        )

      case Channel.Y2(fieldName, agg, scale, axis) =>
        val yFieldType = data(fieldName).head match
          case _: DataValue.Quantitative => VegaEncoding.FieldType.Quantitative
          case _: DataValue.Nominal      => VegaEncoding.FieldType.Nominal

        val aggProp = agg.map(a => VegaEncoding.ChannelProp.Aggregate(a))
        val scaleProp = scale.map(s =>
          VegaEncoding.ChannelProp.Scale(
            VegaEncoding.ScaleSpec.IncludeZero(s.axisIncludesZero)
          )
        )
        val axisProp = axis.map(a =>
          VegaEncoding.ChannelProp.Axis(
            Seq(
              VegaEncoding.AxisProp.LabelFontSize(a.labelFontSize),
              VegaEncoding.AxisProp.TitleFontSize(a.titleFontSize)
            )
          )
        )
        VegaEncoding.Channel.Y2(
          fieldName,
          yFieldType,
          aggProp.toSeq ++ scaleProp.toSeq ++ axisProp.toSeq
        )

      case Channel.Color(fieldName) =>
        VegaEncoding.Channel.Color(fieldName, Seq.empty)
      case Channel.Size(fieldName) =>
        VegaEncoding.Channel.Size(fieldName, Seq.empty)
    ChartWithEncoding(data, encoding = VegaEncoding(vegaChannels))

case class ChartWithEncoding(
    data: Map[String, Seq[DataValue]],
    encoding: VegaEncoding = VegaEncoding(Seq.empty)
):

  def markLine(): ChartWithViews =
    ChartWithViews(data, LayeredView(Seq(SingleView(VegaMark.Line, encoding))))
  def markCircle(): ChartWithViews =
    ChartWithViews(data, LayeredView(Seq(SingleView(VegaMark.Circle, encoding))))
  def markPoint(): ChartWithViews =
    ChartWithViews(data, LayeredView(Seq(SingleView(VegaMark.Point, encoding))))
  def markBar(): ChartWithViews =
    ChartWithViews(data, LayeredView(Seq(SingleView(VegaMark.Bar, encoding))))
  def markArea(): ChartWithViews =
    ChartWithViews(data, LayeredView(Seq(SingleView(VegaMark.Area, encoding))))
  def markBoxplot(): ChartWithViews =
    ChartWithViews(
      data,
      LayeredView(Seq(SingleView(VegaMark.Boxplot, encoding)))
    )
  def markErrorBand(): ChartWithViews =
    ChartWithViews(
      data,
      LayeredView(Seq(SingleView(VegaMark.ErrorBand, encoding)))
    )

trait CompleteChart:
  def data: Map[String, Seq[DataValue]]
  def view: VegaView

  def properties(properties : ChartProperties) : CompleteChartWithProperties =
    CompleteChartWithProperties(
      data,
      view,
      properties
    )

  def vegaSpec : VegaChart = VegaChart(data, view)
  def show()(using plotTarget : PlotTarget) : Unit =  vegaSpec.show()

  def hConcat(other: CompleteChart): ChartWithCompositeView =
    ChartWithCompositeView(data, HConcatViews(Seq(this.view, other.view)))

  def vConcat(other: CompleteChart): ChartWithCompositeView =
    ChartWithCompositeView(data, VConcatViews(Seq(this.view, other.view)))

case class CompleteChartWithProperties(
    data: Map[String, Seq[DataValue]],
    view: VegaView,
    properties: ChartProperties
) extends CompleteChart:

  override def vegaSpec : VegaChart = VegaChart(data, view, VegaTitle(properties.title, Seq(TitleProp.FontSize(properties.titleFontSize))), properties.width, properties.height)


case class ChartWithViews(
    data: Map[String, Seq[DataValue]],
    view: LayeredView
) extends CompleteChart:

  def overlay(other: ChartWithViews): ChartWithViews =
    require(other.data == data, "Data must be the same")
    ChartWithViews(data, LayeredView(this.view.views ++ other.view.views))

case class ChartWithCompositeView(
    data: Map[String, Seq[DataValue]],
    view: CompositeView
) extends CompleteChart

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
      agg: Option[VegaEncoding.AggregateType] = None,
      scale: Option[Scale] = Some(Scale(axisIncludesZero = false)),
      axis: Option[Axis] = None
  ) extends Channel(fieldName):

    def count(): Channel =
      Channel.Y(fieldName, Some(VegaEncoding.AggregateType.Count), scale)

    def scale(scale: Scale): Channel.Y =
      Channel.Y(fieldName, agg, Some(scale))

    def axis(axis: Axis): Channel.Y =
      Channel.Y(fieldName, agg, scale, Some(axis))

  case class Y2(
      fieldName: String,
      agg: Option[VegaEncoding.AggregateType] = None,
      scale: Option[Scale] = Some(Scale(axisIncludesZero = false)),
      axis: Option[Axis] = None
  ) extends Channel(fieldName):

    def count(): Channel =
      Channel.Y2(fieldName, Some(VegaEncoding.AggregateType.Count), scale)

    def scale(scale: Scale): Channel.Y2 =
      Channel.Y2(fieldName, agg, Some(scale), axis)

    def axis(axis: Axis): Channel.Y2 =
      Channel.Y2(fieldName, agg, scale, Some(axis))

  case class Color(fieldName: String) extends Channel(fieldName)
  case class Size(fieldName: String) extends Channel(fieldName)

case class Scale(axisIncludesZero: Boolean = false)

case class Axis(labelFontSize: Int = 14, titleFontSize: Int = 14)


case class ChartProperties(title : String = "", titleFontSize : Int = 20, width : Int = 600, height : Int = 600)

@main def plotSpec(): Unit =
  val xs = Seq.range(0, 200).map(_ / 10.0)
  val ys = xs.map(x => math.sin(x) + math.cos(x))
  val data = Map(
    "x" -> xs.map(DataValue.Quantitative(_)),
    "y" -> ys.map(DataValue.Quantitative(_))
  )

