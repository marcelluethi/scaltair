/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package scaltair

import scaltair.vegalite.TitleProp
import scaltair.vegalite.VegaMark
import scaltair.vegalite.VegaEncoding
import scaltair.vegalite.{VegaView}
import scaltair.vegalite.LayeredView

import scaltair.vegalite.VegaChart
import scaltair.vegalite.VegaTitle
import scaltair.vegalite.TitleProp

import scaltair.vegalite.SingleView
import scaltair.vegalite.HConcatViews
import scaltair.vegalite.CompositeView
import scaltair.vegalite.VConcatViews
import scaltair.PlotTarget
import scaltair.Data
import scaltair.Data.ColumnData

import scaltair.PlotTargetBrowser.given

case class Chart(
    data: ColumnData
):

  def encode(channels: Channel*): ChartWithEncoding =

    def fieldTypeToVegaFieldType(fieldType: FieldType): VegaEncoding.FieldType =
      fieldType match
        case FieldType.Quantitative => VegaEncoding.FieldType.Quantitative
        case FieldType.Ordinal      => VegaEncoding.FieldType.Ordinal
        case FieldType.Nominal      => VegaEncoding.FieldType.Nominal
        case FieldType.Temporal     => VegaEncoding.FieldType.Temporal

    def binPropsFromBinned(bin: Option[Bin]): Option[VegaEncoding.ChannelProp] =
      bin match
        case Some(Bin.Auto) => Some(VegaEncoding.ChannelProp.Bin(VegaEncoding.BinConfig.AutoBin(true)))
        case Some(Bin.MaxBins(i)) => Some(VegaEncoding.ChannelProp.Bin(VegaEncoding.BinConfig.MaxBins(i)))
        case _ => None


    def scalePropsFromScale(scale : Option[Scale]): Option[VegaEncoding.ChannelProp] =
      val scalePropRange = 
          for 
            s <- scale
            r <- s.range
          yield 
            VegaEncoding.ChannelProp.Scale(VegaEncoding.ScaleSpec.FromDomain(VegaEncoding.Domain(r.min, r.max)))
        
      val scalePropIncludeZero = 
          for
            s <- scale
          yield 
            VegaEncoding.ChannelProp.Scale(VegaEncoding.ScaleSpec.IncludeZero(s.axisIncludesZero))
      if scalePropRange.isDefined then 
        scalePropRange
      else scalePropIncludeZero
      
    val vegaChannels = for channel <- channels yield channel match
      case Channel.X(
        fieldName, 
        fieldType, 
        binned, 
        scale, 
        axis) =>
        
        
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
            fieldTypeToVegaFieldType(fieldType),
            binPropsFromBinned(binned).toSeq ++ scalePropsFromScale(scale) ++ axisProp.toSeq
          )
      case Channel.Y(fieldName, fieldType, agg, scale, axis) =>
        val aggProp = agg.map(a => VegaEncoding.ChannelProp.Aggregate(a))      
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
          fieldTypeToVegaFieldType(fieldType),
          aggProp.toSeq ++ scalePropsFromScale(scale).toSeq ++ axisProp.toSeq
        )

      case Channel.Y2(fieldName, fieldType, agg, scale, axis) =>
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
          fieldTypeToVegaFieldType(fieldType),
          aggProp.toSeq ++ scaleProp.toSeq ++ axisProp.toSeq
        )

      case Channel.Color(fieldName) =>
        VegaEncoding.Channel.Color(fieldName, Seq.empty)
      case Channel.Size(fieldName) =>
        VegaEncoding.Channel.Size(fieldName, Seq.empty)
    ChartWithEncoding(data, encoding = VegaEncoding(vegaChannels))

case class ChartWithEncoding(
    data: ColumnData,
    encoding: VegaEncoding = VegaEncoding(Seq.empty)
):

  def markLine(): ChartWithViews =
    ChartWithViews(data, LayeredView(Seq(SingleView(VegaMark.Line, encoding))))
  def markCircle(): ChartWithViews =
    ChartWithViews(
      data,
      LayeredView(Seq(SingleView(VegaMark.Circle, encoding)))
    )
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
  def data: ColumnData
  def view: VegaView

  def properties(properties: ChartProperties): CompleteChartWithProperties =
    CompleteChartWithProperties(
      data,
      view,
      properties
    )

  def vegaSpec: VegaChart = VegaChart(data, view)
  def show()(using plotTarget: PlotTarget): Unit = vegaSpec.show()

  def hConcat(other: CompleteChart): ChartWithCompositeView =
    ChartWithCompositeView(data, HConcatViews(Seq(this.view, other.view)))

  def vConcat(other: CompleteChart): ChartWithCompositeView =
    ChartWithCompositeView(data, VConcatViews(Seq(this.view, other.view)))

case class CompleteChartWithProperties(
    data: ColumnData,
    view: VegaView,
    properties: ChartProperties
) extends CompleteChart:

  override def vegaSpec: VegaChart = VegaChart(
    data,
    view,
    VegaTitle(
      properties.title,
      Seq(TitleProp.FontSize(properties.titleFontSize))
    ),
    properties.width,
    properties.height
  )

case class ChartWithViews(
    data: ColumnData,
    view: LayeredView
) extends CompleteChart:

  def overlay(other: ChartWithViews): ChartWithViews =
    require(other.data == data, "Data must be the same")
    ChartWithViews(data, LayeredView(this.view.views ++ other.view.views))

case class ChartWithCompositeView(
    data: ColumnData,
    view: CompositeView
) extends CompleteChart

sealed trait Channel(
    fieldName: String,
    fieldType: FieldType
)

object Channel:
  case class X(
      fieldName: String,
      fieldType: FieldType,
      bins: Option[Bin] = None,
      scale: Option[Scale] = Some(Scale(axisIncludesZero = false)),
      axis: Option[Axis] = None
  ) extends Channel(fieldName, fieldType):

    def scale(scale: Scale): Channel.X =
      Channel.X(fieldName, fieldType, bins, Some(scale), axis)

    def axis(axis: Axis): Channel.X =
      Channel.X(fieldName, fieldType, bins, scale, Some(axis))

    def binned(): Channel.X =
      Channel.X(fieldName, fieldType, Some(Bin.Auto), scale, axis)
  
    def binned(maxBins : Int): Channel.X =
      Channel.X(fieldName, fieldType, Some(Bin.MaxBins(maxBins)), scale, axis)
  
  case class Y(
      fieldName: String,
      fieldType: FieldType,
      agg: Option[VegaEncoding.AggregateType] = None,
      scale: Option[Scale] = Some(Scale(axisIncludesZero = false)),
      axis: Option[Axis] = None
  ) extends Channel(fieldName, fieldType):

    def count(): Channel =
      Channel.Y(
        fieldName,
        fieldType,
        Some(VegaEncoding.AggregateType.Count),
        scale
      )

    def scale(scale: Scale): Channel.Y =
      Channel.Y(fieldName, fieldType, agg, Some(scale))

    def axis(axis: Axis): Channel.Y =
      Channel.Y(fieldName, fieldType, agg, scale, Some(axis))

  case class Y2(
      fieldName: String,
      fieldType: FieldType,
      agg: Option[VegaEncoding.AggregateType] = None,
      scale: Option[Scale] = Some(Scale(axisIncludesZero = false)),
      axis: Option[Axis] = None
  ) extends Channel(fieldName, fieldType):

    def count(): Channel =
      Channel.Y2(
        fieldName,
        fieldType,
        Some(VegaEncoding.AggregateType.Count),
        scale
      )

    def scale(scale: Scale): Channel.Y2 =
      Channel.Y2(fieldName, fieldType, agg, Some(scale), axis)

    def axis(axis: Axis): Channel.Y2 =
      Channel.Y2(fieldName, fieldType, agg, scale, Some(axis))

  case class Color(fieldName: String)
      extends Channel(fieldName, FieldType.Nominal)
  case class Size(fieldName: String)
      extends Channel(fieldName, FieldType.Quantitative)

case class Scale(axisIncludesZero : Boolean = false, range : Option[Range] = None):
  def range(range : Range) : Scale = this.copy(range = Some(range))
  def axisIncludesZero(axisIncludesZero : Boolean) : Scale = this.copy(axisIncludesZero = axisIncludesZero)

object Scale:
  def withRange(range : Range) : Scale = Scale(range = Some(range))
  def includesZero : Scale = Scale(axisIncludesZero = true)

case class Axis(labelFontSize: Int = 14, titleFontSize: Int = 14)

case class ChartProperties(
    title: String = "",
    titleFontSize: Int = 20,
    width: Int = 600,
    height: Int = 600
)

enum Bin:
  case Auto extends Bin
  case MaxBins(maxBins: Int) extends Bin

enum FieldType:
  case Quantitative, Nominal, Ordinal, Temporal
