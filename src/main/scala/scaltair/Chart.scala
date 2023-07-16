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


/**
 * Main interface to create charts.
 * A chart using the following steps:
  * 1. Calling the `Chart` object to create a chart with a given data set.
  * 2. Calling the `encode` method to specify the channels for the chart.
  * 3. Calling the `markXXX` method to specify the mark type for the chart.
  *    In this step, we can optionally call `overlay` to add layers, and `hconcat` and `vconcat` 
  * to create composite charts. 
  * 4. Finally, calling the `properties` method to specify the properties of the chart.
  */
object Chart:

  /**
    * Create a chart with the given data set.
    */
  def apply(data : ColumnData) : ChartData = ChartData(data)

  case class ChartData(
      data: ColumnData
  ):

    def encode(channels: Channel*): ChartWithEncoding =
      ChartWithEncoding(data, channels)

  case class ChartWithEncoding(
      data: ColumnData,
      channels: Seq[Channel]
  ):

    def markLine(): ChartWithSingleView =
      ChartWithSingleView(data, SingleView(MarkType.Line, channels))
    def markCircle(): ChartWithSingleView =
      ChartWithSingleView(data, SingleView(MarkType.Circle, channels))
    def markRect(): ChartWithSingleView =
      ChartWithSingleView(data, SingleView(MarkType.Rect, channels))
    def markPoint(): ChartWithSingleView =
      ChartWithSingleView(data, SingleView(MarkType.Point, channels))
    def markBar(): ChartWithSingleView =
      ChartWithSingleView(data, SingleView(MarkType.Bar, channels))
    def markArea(): ChartWithSingleView =
      ChartWithSingleView(data, SingleView(MarkType.Area, channels))
    def markBoxplot(): ChartWithSingleView =
      ChartWithSingleView(data, SingleView(MarkType.Boxplot, channels))
    def markErrorBand(): ChartWithSingleView =
      ChartWithSingleView(data, SingleView(MarkType.ErrorBand, channels))

  trait CompleteChart:
    self : CompleteChart =>

    def properties(properties: ChartProperties): CompleteChartWithProperties =
      CompleteChartWithProperties(
        self,
        properties
      )

    def spec: VegaLiteDSL =
      DSLToVegaSpec.createVegeLiteSpec(self)

    def show()(using plotTarget: PlotTarget): Unit = plotTarget.show(spec)

    def hConcat(other: CompleteChart): HConcatChart =
      HConcatChart(self, other)
    def vConcat(other: CompleteChart): VConcatChart =
      VConcatChart(self, other)

  case class CompleteChartWithProperties(
      chart: CompleteChart,
      properties: ChartProperties
  ) extends CompleteChart

  case class ChartWithSingleView(
      data: ColumnData,
      view: SingleView
  ) extends CompleteChart:

    def overlay(other: ChartWithSingleView): ChartWithLayeredView =
      ChartWithLayeredView(data, LayeredView(Seq(view, other.view)))

  case class ChartWithLayeredView(
      data: ColumnData,
      view: LayeredView
  ) extends CompleteChart:

    def overlay(other: ChartWithSingleView): ChartWithLayeredView =
      ChartWithLayeredView(data, LayeredView(view.views :+ other.view))

  case class HConcatChart(
      leftChart: CompleteChart,
      rightChart: CompleteChart
  ) extends CompleteChart

  case class VConcatChart(
      upperChart: CompleteChart,
      lowerChart: CompleteChart
  ) extends CompleteChart

case class ChartProperties(
    title: String = "",
    titleFontSize: Int = 20,
    width: Int = 600,
    height: Int = 600
)
