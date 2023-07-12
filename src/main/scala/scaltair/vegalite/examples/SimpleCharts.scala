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

import scaltair.vegalite.InlineDataset
import scaltair.vegalite.URLData
import scaltair.vegalite.EdEncoding
import scaltair.vegalite.{XClass, YClass, ColorClass, SizeClass}
import scaltair.vegalite.Type
import scaltair.vegalite.ViewBackground
import scaltair.vegalite.VegaLiteDSL
import scaltair.PlotTargetBrowser
import scaltair.vegalite.NonArgAggregateOp

//import scaltair.PlotTargetBrowser.given

/** Example charts, which show how to use the vega lite specification directly.
  */
object SimpleCharts {

  def barChart(): Unit =

    val values: InlineDataset =
      Seq(
        Map("x" -> Some("A"), "y" -> Some(5)),
        Map("x" -> Some("B"), "y" -> Some(3)),
        Map("x" -> Some("C"), "y" -> Some(6)),
        Map("x" -> Some("D"), "y" -> Some(7)),
        Map("x" -> Some("E"), "y" -> Some(2))
      )

    val data = URLData(values = Some(values))
    val mark = "bar"
    val encoding = EdEncoding(
      x = Some(XClass(field = Some("x"), `type` = Some(Type.nominal))),
      y = Some(YClass(field = Some("y"), `type` = Some(Type.quantitative)))
    )

    val view = ViewBackground()

    val spec = VegaLiteDSL(
      title = Some("Bar Chart"),
      width = Some(800.0),
      height = Some(800.0),
      data = Some(data),
      encoding = Some(encoding),
      mark = Some(mark),
      view = Some(view)
    )

    PlotTargetBrowser.show(spec)

  def scatterPlot(): Unit =

    val values: InlineDataset =
      Seq(
        Map("x" -> Some(1), "y" -> Some(5)),
        Map("x" -> Some(2), "y" -> Some(3)),
        Map("x" -> Some(3), "y" -> Some(6)),
        Map("x" -> Some(4), "y" -> Some(7)),
        Map("x" -> Some(5), "y" -> Some(2))
      )

    val data = URLData(values = Some(values))
    val mark = "circle"
    val encoding = EdEncoding(
      x = Some(XClass(field = Some("x"), `type` = Some(Type.nominal))),
      y = Some(YClass(field = Some("y"), `type` = Some(Type.quantitative)))
    )

    val view = ViewBackground()

    val spec = VegaLiteDSL(
      title = Some("Scatterplot"),
      width = Some(800.0),
      height = Some(800.0),
      data = Some(data),
      encoding = Some(encoding),
      mark = Some(mark),
      view = Some(view)
    )

    PlotTargetBrowser.show(spec)

  def lineSeriesPlot(): Unit =

    val values: InlineDataset =
      Seq(
        Map("x" -> Some(1), "y" -> Some(5), "series" -> Some("line1")),
        Map("x" -> Some(2), "y" -> Some(3), "series" -> Some("line1")),
        Map("x" -> Some(3), "y" -> Some(6), "series" -> Some("line1")),
        Map("x" -> Some(1), "y" -> Some(10), "series" -> Some("line2")),
        Map("x" -> Some(2), "y" -> Some(6), "series" -> Some("line2")),
        Map("x" -> Some(3), "y" -> Some(12), "series" -> Some("line2"))
      )
    val data = URLData(values = Some(values))
    val mark = "line"
    val encoding = EdEncoding(
      x = Some(XClass(field = Some("x"), `type` = Some(Type.quantitative))),
      y = Some(YClass(field = Some("y"), `type` = Some(Type.quantitative))),
      color =
        Some(ColorClass(field = Some("series"), `type` = Some(Type.nominal)))
    )

    val view = ViewBackground()

    val spec = VegaLiteDSL(
      title = Some("Line series"),
      width = Some(800.0),
      height = Some(800.0),
      data = Some(data),
      encoding = Some(encoding),
      mark = Some(mark),
      view = Some(view)
    )

    PlotTargetBrowser.show(spec)

  def histogram(): Unit =

    val xs = Seq.fill(1000)(scala.util.Random.nextGaussian())

    val values = xs.map(v => Map("x" -> Some(v)))
    val data = URLData(values = Some(values))

    val mark = "bar"
    val encoding = EdEncoding(
      x = Some(
        XClass(
          field = Some("x"),
          `type` = Some(Type.quantitative),
          bin = Some(true)
        )
      ),
      y = Some(
        YClass(
          field = Some("x"),
          `type` = Some(Type.quantitative),
          aggregate = Some(NonArgAggregateOp.count)
        )
      )
    )

    val spec = VegaLiteDSL(
      title = Some("Line series"),
      width = Some(800.0),
      height = Some(800.0),
      data = Some(data),
      encoding = Some(encoding),
      mark = Some(mark)
    )

    PlotTargetBrowser.show(spec)

  def bubblePlot(): Unit =

    val values: InlineDataset =
      Seq(
        Map(
          "x" -> Some(1),
          "y" -> Some(2),
          "size" -> Some(1),
          "color" -> Some(0)
        ),
        Map(
          "x" -> Some(2),
          "y" -> Some(4),
          "size" -> Some(2),
          "color" -> Some(0)
        ),
        Map(
          "x" -> Some(3),
          "y" -> Some(8),
          "size" -> Some(4),
          "color" -> Some(0)
        ),
        Map(
          "x" -> Some(4),
          "y" -> Some(16),
          "size" -> Some(8),
          "color" -> Some(0)
        ),
        Map(
          "x" -> Some(5),
          "y" -> Some(32),
          "size" -> Some(5),
          "color" -> Some(1)
        ),
        Map(
          "x" -> Some(6),
          "y" -> Some(64),
          "size" -> Some(2),
          "color" -> Some(1)
        ),
        Map(
          "x" -> Some(7),
          "y" -> Some(128),
          "size" -> Some(1),
          "color" -> Some(1)
        ),
        Map(
          "x" -> Some(8),
          "y" -> Some(256),
          "size" -> Some(9),
          "color" -> Some(1)
        )
      )
    val data = URLData(values = Some(values))

    val mark = "circle"
    val encoding = EdEncoding(
      x = Some(XClass(field = Some("x"), `type` = Some(Type.quantitative))),
      y = Some(YClass(field = Some("y"), `type` = Some(Type.quantitative))),
      size =
        Some(SizeClass(field = Some("size"), `type` = Some(Type.quantitative))),
      color =
        Some(ColorClass(field = Some("color"), `type` = Some(Type.nominal)))
    )

    val view = ViewBackground()

    val spec = VegaLiteDSL(
      title = Some("Line series"),
      width = Some(800.0),
      height = Some(800.0),
      data = Some(data),
      encoding = Some(encoding),
      mark = Some(mark),
      view = Some(view)
    )

    PlotTargetBrowser.show(spec)

  @main def runSimpleCharts() =
    // barChart()
    // scatterPlot()
    // lineSeriesPlot()
    histogram()
    // bubblePlot()
}
