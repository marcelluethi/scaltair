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
package scaltair.vegalite.examples

import scaltair.vegalite.{VegaChart, VegaView, VegaEncoding}
import scaltair.vegalite.SingleView
import scaltair.vegalite.VegaMark
import scaltair.plottarget.PlotTargets.plotTargetBrowser
import scaltair.vegalite.VegaTitle

/** Example charts, which show how to use the vega lite specification directly.
  * This is useful if you want maximum control over the plot.
  */
object SimpleCharts {

  def barChart(): Unit =
    val data = Map(
      "x" -> Seq("A", "B", "C", "D", "E"),
      "y" -> Seq(5, 3, 6, 7, 2)
    )
    val view = SingleView(
      mark = VegaMark.Bar,
      encoding = VegaEncoding(channels =
        Seq(
          VegaEncoding.Channel.X("x", VegaEncoding.FieldType.Nominal),
          VegaEncoding.Channel.Y("y", VegaEncoding.FieldType.Quantitative)
        )
      )
    )
    val chart = VegaChart(data, view, VegaTitle("bar"))
    chart.show()

  def scatterPlot(): Unit =
    val data = Map(
      "x" -> Seq(1, 2, 3, 4, 5),
      "y" -> Seq(5, 3, 6, 7, 2)
    )
    val view = SingleView(
      mark = VegaMark.Point,
      encoding = VegaEncoding(channels =
        Seq(
          VegaEncoding.Channel.X("x", VegaEncoding.FieldType.Quantitative),
          VegaEncoding.Channel.Y("y", VegaEncoding.FieldType.Quantitative)
        )
      )
    )
    val chart = VegaChart(data, view, VegaTitle("scatter"))
    chart.show()

  def linePlot(): Unit =
    val xs = Seq.range(0, 200).map(_ / 10.0)
    val ys = xs.map(x => math.sin(x) + math.cos(x))
    val data = Map(
      "x" -> xs,
      "y" -> ys
    )
    val view = SingleView(
      mark = VegaMark.Line,
      encoding = VegaEncoding(channels =
        Seq(
          VegaEncoding.Channel.X("x", VegaEncoding.FieldType.Quantitative),
          VegaEncoding.Channel.Y("y", VegaEncoding.FieldType.Quantitative)
        )
      )
    )
    val chart = VegaChart(data, view, VegaTitle("line"))
    chart.show()

  def lineSeriesPlot(): Unit =
    val xs = Seq.range(0, 200).map(_ / 10.0)
    val ys = xs.map(x => math.sin(x) + math.cos(x))
    val zs = xs.map(x => math.sin(x) - math.cos(x))

    val data = Map(
      "x" -> (xs ++ xs),
      "y" -> (ys ++ zs),
      "series" -> (Seq.fill(xs.length)("sin") ++ Seq.fill(
        xs.length
      )("cos"))
    )
    val view = SingleView(
      mark = VegaMark.Line,
      encoding = VegaEncoding(channels =
        Seq(
          VegaEncoding.Channel.X("x", VegaEncoding.FieldType.Quantitative),
          VegaEncoding.Channel.Y("y", VegaEncoding.FieldType.Quantitative),
          VegaEncoding.Channel.Color("series")
        )
      )
    )
    val chart = VegaChart(data, view, VegaTitle("line"))
    chart.show()

  def histogram(): Unit =
    val xs = Seq.fill(1000)(scala.util.Random.nextGaussian())

    val data = Map(
      "x" -> xs
    )

    val encoding = VegaEncoding(
      Seq(
        VegaEncoding.Channel.X(
          "x",
          VegaEncoding.FieldType.Quantitative,
          Seq(VegaEncoding.ChannelProp.Bin(true))
        ),
        VegaEncoding.Channel.Y(
          "x",
          VegaEncoding.FieldType.Quantitative,
          Seq(
            VegaEncoding.ChannelProp.Aggregate(VegaEncoding.AggregateType.Count)
          )
        )
      )
    )

    val view = SingleView(
      mark = VegaMark.Bar,
      encoding = encoding
    )

    val chart = VegaChart(data, view, VegaTitle("line"))
    chart.show()

  def bubblePlot(): Unit =

    val xs = Seq.range(-10, 10)
    val data = Map(
      "x" -> xs,
      "y" -> xs,
      "size" -> Seq.range(0, xs.length),
      "color" -> xs
        .map(x => if x < 0 then 1 else 2)
    )

    val encoding = VegaEncoding(
      Seq(
        VegaEncoding.Channel.X(
          "x",
          VegaEncoding.FieldType.Quantitative,
          Seq(VegaEncoding.ChannelProp.Bin(true))
        ),
        VegaEncoding.Channel.Y(
          "y",
          VegaEncoding.FieldType.Quantitative
        ),
        VegaEncoding.Channel.Size(
          "size"
        ),
        VegaEncoding.Channel.Color(
          "size"
        )
      )
    )

    val view = SingleView(
      mark = VegaMark.Circle,
      encoding = encoding
    )

    val chart = VegaChart(data, view, VegaTitle("line"))
    chart.show()

  @main def runSimpleCharts() =
    barChart()
    scatterPlot()
    linePlot()
    lineSeriesPlot()
    histogram()
    bubblePlot()
}
