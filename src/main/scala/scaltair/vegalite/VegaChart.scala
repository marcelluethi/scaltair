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
package scaltair.vegalite

import scaltair.vegalite.VegaMark
import scaltair.vegalite.VegaEncoding
import scaltair.vegalite.VegaData
import scaltair.json.JsonObject
import scaltair.json.JsonString
import scaltair.json.JsonNumber
import scaltair.json.Json
import scaltair.vegalite.VegaLite
import scaltair.json.JsonArray
import scaltair.PlotTarget
import scaltair.json.JsonValue
import scaltair.vegalite.{
  HConcatViews,
  VConcatViews,
  CompositeView,
  SingleView,
  LayeredView
}

import scaltair.Data.ColumnData

case class VegaChart(
    data: ColumnData,
    val view: VegaView,
    title: VegaTitle = VegaTitle(""),
    width: Int = 600,
    height: Int = 600
) extends VegaLite:

  override def spec: JsonObject =
    val dataspec = VegaData(data).spec

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
      "title" -> title.spec,
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


    // data comes last, as this is most convenient
    // when the confiuration options are first when working with vega-editor
    val dataSpec =   "data" -> dataspec
    JsonObject(baseSpec ++ viewSpec :+ dataSpec)

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
        JsonObject(Seq("vconcat" -> collectSpecForCompositeView(concatView)))
    JsonArray(viewSpecs)

  def show()(using plotTarget: PlotTarget): Unit =
    plotTarget.show(this)
    Thread.sleep(
      1000
    ) // TODO replace me with a proper wait for the browser to open
