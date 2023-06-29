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
package scaltair.examples

import scaltair.Chart
import scaltair.ChartProperties
import scaltair.Channel
import scaltair.Scale
import scaltair.ChartWithViews
import scaltair.FieldType

import scaltair.PlotTargetBrowser.given

object CompositeCharts:

  val data = Map(
    "shoe-size" -> Seq(38, 42, 43, 44, 47),
    "stature" -> Seq(150, 170, 172, 180, 195),
    "weight" -> Seq(55, 75, 70, 75, 100),
    "sex" -> Seq("f", "f", "m", "m", "m")
  )

  def layeredChart(): Unit =

    val view1 = Chart(data)
      .encode(
        Channel.X("shoe-size", FieldType.Ordinal),
        Channel.Y("stature", FieldType.Quantitative)
      )
      .markLine()

    val view2 = Chart(data)
      .encode(
        Channel.X("shoe-size", FieldType.Ordinal),
        Channel.Y("stature", FieldType.Quantitative),
        Channel.Color("sex", FieldType.Nominal)
      )
      .markBar()

    view1.overlay(view2).show()

  def stackedChart(): Unit =

    val view1 = Chart(data)
      .encode(
        Channel.X("shoe-size", FieldType.Ordinal),
        Channel.Y("stature", FieldType.Quantitative)
      )
      .markLine()

    val view2 = Chart(data)
      .encode(
        Channel.X("shoe-size", FieldType.Ordinal),
        Channel.Y("weight", FieldType.Quantitative)
      )
      .markLine()

    view1
      .hConcat(view2)
      .properties(ChartProperties(title = "stacked charts"))
      .show()

  @main def runCompositeCharts(): Unit =
    layeredChart()
    stackedChart()
