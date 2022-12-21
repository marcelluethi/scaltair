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
package scalismo.plot.vegalite

import scalismo.plot.vegalite.Mark
import scalismo.plot.data.DataFrame
import scalismo.plot.vegalite.Encoding
import scalismo.plot.vegalite.Data
import scalismo.plot.json.JsonObject
import scalismo.plot.json.JsonString
import scalismo.plot.json.JsonNumber
import scalismo.plot.json.Json
import scalismo.plot.vegalite.VegaLite
import scalismo.plot.json.JsonArray
import scalismo.plot.plottarget.PlotTargetBrowser
import scalismo.plot.plottarget.PlotTarget

case class Chart(
    dataFrame: DataFrame,
    val view: View,
    title: String = "",
    width: Int = 600,
    height: Int = 600
) extends VegaLite:

  def data = Data(dataFrame)

  override def spec: JsonObject =
    val dataspec = data.spec

    // we divide the spec into base spec and view.
    // Each is just a seq of key-value pairs, which we
    // put together into an JsonObject later.
    // The reason is that the viewspec of vega-lite does not
    // seem to be regular and requires different handling
    // of single views from composite views.
    val baseSpec = Seq(
      "$schema" -> JsonString(
        "https://vega.github.io/schema/vega-lite/v5.json"
      ),
      "description" -> JsonString("."),
      "width" -> JsonNumber(width),
      "height" -> JsonNumber(height),
      "title" -> JsonString(title),
      "data" -> dataspec
    )

    val viewSpec = view match
      case singleView: SingleView =>
        Seq(
          "encoding" -> singleView.encoding.spec,
          "mark" -> singleView.mark.spec
        )
      case layeredView: LayeredView =>
        Seq("layer" -> collectSpecForCompositeView(layeredView))
      case concatView: HConcatViews =>
        Seq("hconcat" -> collectSpecForCompositeView(concatView))
      case concatView: VConcatViews =>
        Seq("vconcat" -> collectSpecForCompositeView(concatView))
    JsonObject(baseSpec ++ viewSpec)

  private def collectSpecForCompositeView(
      compositeView: CompositeView
  ): JsonArray =

    val viewSpecs = for (view <- compositeView.views) yield view match
      case singleView: SingleView =>
        JsonObject(
          Seq(
            "encoding" -> singleView.encoding.spec,
            "mark" -> singleView.mark.spec
          )
        )
      case layeredView: LayeredView =>
        JsonObject(Seq("layer" -> collectSpecForCompositeView(layeredView)))
      case concatView: HConcatViews =>
        JsonObject(Seq("hconcat" -> collectSpecForCompositeView(concatView)))
      case concatView: VConcatViews =>
        JsonObject(Seq("hconcat" -> collectSpecForCompositeView(concatView)))
    JsonArray(viewSpecs)

  def show()(using plotTarget : PlotTarget): Unit =
    plotTarget.show(this)
    Thread.sleep(
      1000
    ) // TODO replace me with a proper wait for the browser to open

// object Chart:

//   def main(args: Array[String]): Unit =
//     val data =
//       DataFrame.fromColumns(Map("x" -> Seq(1, 2, 3), "y" -> Seq(3, 4, 5)))

//     import Encoding.*

//     val encoding = Encoding(
//       Map(
//         Channel.X ->
//           Field("x", FieldType.Quantitative, bin = false, aggregate = None),
//         Channel.Y ->
//           Field("x", FieldType.Quantitative, bin = false, aggregate = None))
//     )

//     val view1 = SingleView()
//       .withMark(Mark(Mark.MarkType.Line))
//       .encode(encoding)
//     val view2 = SingleView()
//       .withMark(Mark(Mark.MarkType.Bar))
//       .encode(encoding)

//     val compositeView =  VConcatViews(
//           Seq(view1, view2, LayeredView(Seq(view1, view2)))
//         )

//     Chart(data, compositeView).show()
