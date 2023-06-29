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
import scaltair.PlotTargetBrowser.given
import scaltair.Channel
import scaltair.Scale
import scaltair.ChartProperties
import scaltair.FieldType
import scaltair.Data

/** Example charts, which show how to use the vega lite specification directly.
  * This is useful if you want maximum control over the plot.
  */
object SimpleCharts:

  def barChart(): Unit =
    val data = Map(
      "x" -> Seq("A", "B", "C", "D", "E"),
      "y" -> Seq(5, 3, 6, 7, 2)
    )
    Chart(data)
      .encode(
        Channel.X("x", FieldType.Nominal),
        Channel.Y("y", FieldType.Quantitative)
      )
      .markBar()
      .show()

  def scatterPlot(): Unit =
    val data = Map(
      "x" -> Seq(1, 2, 3, 4, 5),
      "y" -> Seq(5, 3, 6, 7, 2)
    )

    Chart(data)
      .encode(
        Channel.X("x", FieldType.Quantitative),
        Channel.Y("y", FieldType.Quantitative)
      )
      .markPoint()
      .show()

  def linePlot(): Unit =
    val xs = Seq.range(0, 200).map(_ / 10.0)
    val ys = xs.map(x => math.sin(x) + math.cos(x))
    val data = Map(
      "x" -> xs,
      "y" -> ys
    )

    Chart(data)
      .encode(
        Channel.X("x", FieldType.Quantitative).scale(Scale.withRange(0 to 10)),
        Channel.Y("y", FieldType.Quantitative).scale(Scale.withRange(0 to 10))
      )
      .markLine()
      .properties(ChartProperties(title = "line", titleFontSize = 20))
      .show()

  def lineSeries(): Unit =
    val xs = Seq.range(0, 200).map(_ / 10.0)
    val ys = xs.map(x => math.sin(x) + math.cos(x))
    val zs = xs.map(x => math.sin(x) - math.cos(x))

    val data = Map(
      "x" -> (xs ++ xs),
      "y" -> (ys ++ zs),
      "series" -> (Seq.fill(xs.length)("sin") ++ Seq.fill(xs.length)("cos"))
    )
    Chart(data)
      .encode(
        Channel.X("x", FieldType.Quantitative),
        Channel.Y("y", FieldType.Quantitative),
        Channel.Color("series", FieldType.Quantitative)
      )
      .markLine()
      .show()

  def histogram(): Unit =
    val xs = Seq.fill(1000)(scala.util.Random.nextGaussian())

    val data = Map(
      "x" -> xs
    )

    Chart(data)
      .encode(
        Channel.X("x", FieldType.Quantitative).binned(),
        Channel.Y("x", FieldType.Quantitative).count()
      )
      .markBar()
      .show()

  def bubblePlot(): Unit =

    val xs = Seq.range(-10, 10)
    val data = Map(
      "x" -> xs,
      "y" -> xs.map(x => x * x),
      "size" -> Seq.range(0, xs.length),
      "color" -> xs
        .map(x => if x < 0 then 1 else 2)
    )

    Chart(data)
      .encode(
        Channel.X("x", FieldType.Quantitative).binned(),
        Channel.Y("y", FieldType.Quantitative),
        Channel.Size("size"),
        Channel.Color("color", FieldType.Nominal)
      )
      .markCircle()
      .show()

  def heatMap(): Unit =
    val matrixData = Seq(Seq(1, 2, 3), Seq(4, 5, 6), Seq(7, 8, 9))
    val rows = for
      x <- 0 until matrixData.size
      y <- 0 until matrixData(0).size
    yield Map(
      "x" -> x,
      "y" -> y,
      "z" -> matrixData(x)(y)
    )
    Chart(Data.fromRows(rows))
      .encode(
        Channel.X("x", FieldType.Ordinal),
        Channel.Y("y", FieldType.Ordinal),
        Channel.Color("z", FieldType.Quantitative)
      )
      .markRect()
      .show()

  @main def runSimpleCharts() =
    // barChart()
    // scatterPlot()
    // linePlot()
    heatMap()
    // lineSeries()
    // histogram()
    // bubblePlot()
