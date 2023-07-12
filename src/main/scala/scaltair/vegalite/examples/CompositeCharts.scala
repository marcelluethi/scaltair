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
import scaltair.vegalite.LayerSpec
import scaltair.vegalite.Type
import scaltair.vegalite.ViewBackground
import scaltair.vegalite.VegaLiteDSL
import scaltair.PlotTargetBrowser
import scaltair.vegalite.NonArgAggregateOp
import scaltair.vegalite.LayerEncoding
import scaltair.vegalite.Spec
import scaltair.json.Json

//import scaltair.PlotTargetBrowser.given

/** Example charts, which show how to use the vega lite specification directly.
  */
object CompositeCharts {

  def overlay(): Unit =

    val values: InlineDataset =
      Seq(
        Map("x" -> Some(1), "y" -> Some(1)),
        Map("x" -> Some(2), "y" -> Some(4)),
        Map("x" -> Some(3), "y" -> Some(9)),
        Map("x" -> Some(4), "y" -> Some(16)),
        Map("x" -> Some(5), "y" -> Some(25))
      )

    val data = URLData(values = Some(values))

    val view = ViewBackground()

    val layer1Encoding = LayerEncoding(
      x = Some(XClass(field = Some("x"), `type` = Some(Type.quantitative))),
      y = Some(YClass(field = Some("y"), `type` = Some(Type.quantitative)))
    )
    val layer1 = LayerSpec(
      data = Some(data),
      encoding = Some(layer1Encoding),
      mark = Some("line")
    )

    val layer2Encoding = LayerEncoding(
      x = Some(XClass(field = Some("x"), `type` = Some(Type.quantitative))),
      y = Some(YClass(field = Some("y"), `type` = Some(Type.quantitative)))
    )

    val layer2 = LayerSpec(
      data = Some(data),
      encoding = Some(layer2Encoding),
      mark = Some("circle")
    )

    val spec = VegaLiteDSL(
      width = Some(800.0),
      height = Some(800.0),
      data = Some(data),
      layer = Some(Seq(layer1, layer2))
    )

    PlotTargetBrowser.show(spec)

  def concat(): Unit =
    val values: InlineDataset =
      Seq(
        Map("x" -> Some(1), "y" -> Some(1)),
        Map("x" -> Some(2), "y" -> Some(4)),
        Map("x" -> Some(3), "y" -> Some(9)),
        Map("x" -> Some(4), "y" -> Some(16)),
        Map("x" -> Some(5), "y" -> Some(25))
      )

    val data = URLData(values = Some(values))

    val view = ViewBackground()

    val encoding = EdEncoding(
      x = Some(XClass(field = Some("x"), `type` = Some(Type.quantitative))),
      y = Some(YClass(field = Some("y"), `type` = Some(Type.quantitative)))
    )
    val spec1 = Spec(
      encoding = Some(encoding),
      mark = Some("line")
    )

    val spec2 = Spec(
      encoding = Some(encoding),
      mark = Some("circle")
    )

    val spec3 = Spec(
      vconcat = Some(Seq(spec1, spec2))
    )

    val spec4 = Spec(
      vconcat = Some(Seq(spec1, spec2))
    )

    val spec = VegaLiteDSL(
      hconcat = Some(Seq(spec3, spec4)),
      data = Some(data)
    )

    PlotTargetBrowser.show(spec)

  @main def runCompositeCharts() =
    concat()
}
