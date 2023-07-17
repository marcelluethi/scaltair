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

import scaltair.PlotTarget
import scaltair.Data
import scaltair.Data.ColumnData

import scaltair.PlotTargetBrowser.given
import scaltair.Channel.*
import scaltair.vegalite.VegaLiteDSL

/** Main interface to create charts. A chart using the following steps:
  *   1. Calling the `Chart` object to create a chart with a given data set. 2.
  *      Calling the `encode` method to specify the channels for the chart. 3.
  *      Calling the `markXXX` method to specify the mark type for the chart. In
  *      this step, we can optionally call `overlay` to add layers, and
  *      `hconcat` and `vconcat` to create composite charts. 4. Finally, calling
  *      the `properties` method to specify the properties of the chart.
  */
object Chart:

  /** Create a chart with the given data set.
    */
  def apply(data: ColumnData): ChartData = ChartData(data)

  /** The first stage in creating a chart. It just contains the data.
    */
  case class ChartData(
      data: ColumnData
  ):

    /** Specify the encoding (i.e. the channels) for the chart
      *
      * @param channels
      * @return
      */
    def encode(channels: Channel*): ChartWithEncoding =
      ChartWithEncoding(data, channels)

  /** The second stage in creating a chart. It contains the data and the
    * encoding.
    */
  case class ChartWithEncoding(
      data: ColumnData,
      channels: Seq[Channel]
  ):
    /** Specify the visual property (mark) to use for displaying the chart data
      */
    def mark(mark: Mark): ChartWithSingleView =
      ChartWithSingleView(data, SingleView(mark, channels, false, 1.0))

  /** This trait represets a complete chart, in the sense that it contains all
    * the information needed to create a Vega-Lite specification that can be
    * visualized.
    */
  trait CompleteChart:
    self: CompleteChart =>

    /** Any complete chart can be customized by providing chart properties.
      */
    def properties(properties: ChartProperties): CompleteChartWithProperties =
      CompleteChartWithProperties(
        self,
        properties
      )

    /** Create a Vega-Lite specification from the chart.
      */
    def spec: VegaLiteDSL =
      DSLToVegaSpec.createVegeLiteSpec(self)

    /** Show the chart in the given plottarget
      */
    def show()(using plotTarget: PlotTarget): Unit = plotTarget.show(spec)

    /** Concate the chart with another chart horizontally
      */
    def hConcat(other: CompleteChart): HConcatChart =
      HConcatChart(self, other)

    /** Concate the chart with another chart vertically
      */
    def vConcat(other: CompleteChart): VConcatChart =
      VConcatChart(self, other)

  /** A chart with a single view. This is the most basic chart.
    */
  case class ChartWithSingleView(
      data: ColumnData,
      view: SingleView
  ) extends CompleteChart:

    /** Overlay the chart with another chart. Both charts need to have the same
      * data, but can have different encodings.
      */
    def overlay(other: ChartWithSingleView): ChartWithLayeredView =
      require(data == other.data, "Both charts need to have the same data")
      ChartWithLayeredView(data, LayeredView(Seq(view, other.view)))

    /** Clip the mark to the chart size.
      */
    def clip(clipMark: Boolean) =
      ChartWithSingleView(data, view.copy(clip = clipMark))

    def opacity(opacity: Double) =
      ChartWithSingleView(data, view.copy(opacity = opacity))

  /** A chart with a layered view. This is a chart that
    *
    * @param data
    * @param view
    */
  case class ChartWithLayeredView(
      data: ColumnData,
      view: LayeredView
  ) extends CompleteChart:

    /** Overlay the chart with another chart. Both charts need to have the same
      * data, but can have different encodings.
      */
    def overlay(other: ChartWithSingleView): ChartWithLayeredView =
      ChartWithLayeredView(data, LayeredView(view.views :+ other.view))

  /** A chart consisting of two, horizontally concatenated charts.
    */
  case class HConcatChart(
      leftChart: CompleteChart,
      rightChart: CompleteChart
  ) extends CompleteChart

  /** A chart consisting of two, vertically concatenated charts.
    */
  case class VConcatChart(
      upperChart: CompleteChart,
      lowerChart: CompleteChart
  ) extends CompleteChart

  /** Represents a complete chart with custom properties.
    */
  case class CompleteChartWithProperties(
      chart: CompleteChart,
      properties: ChartProperties
  ) extends CompleteChart

/** Properties of a chart.
  */
case class ChartProperties(
    title: String = "",
    titleFontSize: Int = 20,
    width: Int = 600,
    height: Int = 600
)
