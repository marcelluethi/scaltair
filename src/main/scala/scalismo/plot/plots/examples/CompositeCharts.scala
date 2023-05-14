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
package scalismo.plot.plots.examples

import scalismo.plot.Chart
import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
import scalismo.plot.ChartProperties
import scalismo.plot.Channel
import scalismo.plot.Scale
import scalismo.plot.ChartWithViews
import scalismo.plot.FieldType

object CompositeCharts:

  val data = Map(
    "x" -> Seq("A", "B", "C", "D", "E"),
    "y" -> Seq(5, 3, 6, 7, 2),
    "y2" -> Seq(6, 4, 7, 8, 3)
  )

  val view1 = Chart(data)
    .encode(
      Channel.X("x", FieldType.Nominal),
      Channel.Y("y", FieldType.Quantitative)
    )
    .markLine()

  val view2 = Chart(data)
    .encode(
      Channel.X("x", FieldType.Nominal),
      Channel.Y("y", FieldType.Quantitative),
      Channel.Size("y")
    )
    .markPoint()

  def layeredChart(): Unit =
    view1
      .overlay(view2)
      .show()

  def stackedChart(): Unit =

    val verticalCharts = (view1
      .vConcat(view2))
      .hConcat(view1.vConcat(view2))
      .properties(ChartProperties(title = "Vertical charts"))
      .show()

  def chartWithTwoAxis(): Unit =
    val base = Chart(data)
      .encode(
        Channel.X("x", FieldType.Nominal),
        Channel.Y("y", FieldType.Quantitative)
      )
      .markArea()
    val line = Chart(data)
      .encode(
        Channel.X("x", FieldType.Nominal),
        Channel.Y("y2", FieldType.Quantitative)
      )
      .markPoint()

    base.overlay(line).show()

  @main def runCompositeCharts(): Unit =
    layeredChart()
    stackedChart()
    chartWithTwoAxis()
