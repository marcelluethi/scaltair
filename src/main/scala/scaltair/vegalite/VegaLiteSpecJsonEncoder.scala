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

/** This file contains encodings for the different parts of the vega lite
  * specification. Note that we only encode the parts that are used in the
  * high-level dsl. If you want to add more, you can use the same pattern as
  * below. Note, that while this is repetitive, it is also very easy to add new
  * parts of the specification. Not using a json parser for that was a
  * deliberate choice, in order to minimize dependencies and keep the complexity
  * low.
  */

import scaltair.json.*
import scala.annotation.targetName
import scala.meta.internal.javacp.BaseType.J

extension (dsl: VegaLiteDSL)
  def toJson(): JsonValue =
    val encodingOpt =
      dsl.encoding.map(encoding => "encoding" -> encoding.toJson())
    val markOpt = dsl.mark.map(mark => "mark" -> mark.toJson())
    val dataOpt = dsl.data.map(data => "data" -> data.toJson())

    val layerOpt = dsl.layer.map(layerSeq =>
      "layer" -> JsonArray(layerSeq.map(layer => layer.toJson()))
    )

    val hconcatOpt = dsl.hconcat.map(hconcatSeq =>
      "hconcat" -> JsonArray(hconcatSeq.map(hconcat => hconcat.toJson()))
    )

    val vConcatOpt = dsl.vconcat.map(vconcatSeq =>
      "vconcat" -> JsonArray(vconcatSeq.map(vconcat => vconcat.toJson()))
    )

    val widthOpt = dsl.width.map(width => "width" -> width.toJson())

    val heightOpt = dsl.width.map(height => "height" -> height.toJson())

    val titleOpt = dsl.title.map(title => "title" -> title.toJson())
    val schema =
      "$schema" -> JsonString("https://vega.github.io/schema/vega-lite/v5.json")

    JsonObject(
      schema +: (widthOpt.toSeq ++
        heightOpt.toSeq ++ titleOpt.toSeq ++
        encodingOpt.toSeq ++ markOpt.toSeq ++
        layerOpt.toSeq ++ hconcatOpt ++ vConcatOpt ++
        dataOpt.toSeq)
    )

extension (spec: Spec)
  def toJson(): JsonValue =
    val encodingOpt =
      spec.encoding.map(encoding => "encoding" -> encoding.toJson())
    val markOpt = spec.mark.map(mark => "mark" -> mark.toJson())
    val dataOpt = spec.data.map(data => "data" -> data.toJson())

    val layerOpt = spec.layer.map(layerSeq =>
      "layer" -> JsonArray(layerSeq.map(layer => layer.toJson()))
    )

    val hconcatOpt = spec.hconcat.map(hconcatSeq =>
      "hconcat" -> JsonArray(hconcatSeq.map(hconcat => hconcat.toJson()))
    )

    val vConcatOpt = spec.vconcat.map(vconcatSeq =>
      "vconcat" -> JsonArray(vconcatSeq.map(vconcat => vconcat.toJson()))
    )

    val widthOpt = spec.width.map(width => "width" -> width.toJson())

    val heightOpt = spec.width.map(height => "height" -> height.toJson())

    val titleOpt = spec.title.map(title => "title" -> title.toJson())

    JsonObject(
      encodingOpt.toSeq ++ markOpt.toSeq ++
        layerOpt.toSeq ++ hconcatOpt.toSeq ++ vConcatOpt.toSeq ++
        widthOpt.toSeq ++ heightOpt.toSeq ++ titleOpt.toSeq ++
        dataOpt.toSeq
    )

extension (width: SpecHeight)
  @targetName("widthToJson")
  def toJson(): JsonValue =
    width match
      case width: Double => JsonNumber(width)
      case _ => throw new Exception("No json encoding for SpecHeight != Double")

extension (title: LayerTitle)
  @targetName("titleToJson")
  def toJson(): JsonValue =
    title match
      case title: String => JsonString(title)
      case _ => throw new Exception("No json encoding for LayerTitle != String")

///////////////////////////
// Encoding
///////////////////////////

extension (encoding: EdEncoding)
  def toJson(): JsonValue =
    val xOpt = encoding.x.map(x => "x" -> x.toJson())
    val yOpt = encoding.y.map(y => "y" -> y.toJson())
    val y2Opt = encoding.y2.map(y2 => "y2 " -> y2.toJson())
    val colorOpt = encoding.color.map(color => "color" -> color.toJson())
    val sizeOpt = encoding.size.map(size => "size" -> size.toJson())
    JsonObject(
      xOpt.toSeq ++ yOpt.toSeq ++ colorOpt.toSeq ++ y2Opt.toSeq ++ sizeOpt.toSeq
    )

extension (layerEncoding: LayerEncoding)
  def toJson(): JsonValue =
    val xOpt = layerEncoding.x.map(x => "x" -> x.toJson())
    val yOpt = layerEncoding.y.map(y => "y" -> y.toJson())
    val y2Opt = layerEncoding.y2.map(y2 => "y2 " -> y2.toJson())
    val colorOpt = layerEncoding.color.map(color => "color" -> color.toJson())
    val sizeOpt = layerEncoding.size.map(size => "size" -> size.toJson())
    JsonObject(
      xOpt.toSeq ++ yOpt.toSeq ++ colorOpt.toSeq ++ y2Opt.toSeq ++ sizeOpt.toSeq
    )

extension (x: XClass)
  def toJson(): JsonValue =
    val fieldOpt = x.field.map(field => "field" -> field.toJson())
    val typeOpt = x.`type`.map(`type` => "type" -> `type`.toJson())
    val aggregateOpt =
      x.aggregate.map(aggregate => "aggregate" -> aggregate.toJson())
    val binOpt = x.bin.map(bin => "bin" -> bin.toJson())
    val scaleOpt = x.scale.map(scale => "scale" -> scale.toJson())
    val axisOpt = x.axis.map(axis => "axis" -> axis.toJson())
    JsonObject(
      fieldOpt.toSeq ++ typeOpt.toSeq ++ aggregateOpt.toSeq ++
        binOpt.toSeq ++ scaleOpt.toSeq ++ axisOpt.toSeq
    )

extension (y: YClass)
  def toJson(): JsonValue =
    val fieldOpt = y.field.map(field => "field" -> field.toJson())
    val typeOpt = y.`type`.map(`type` => "type" -> `type`.toJson())
    val aggregateOpt =
      y.aggregate.map(aggregate => "aggregate" -> aggregate.toJson())
    val binOpt = y.bin.map(bin => "bin" -> bin.toJson())
    val scaleOpt = y.scale.map(scale => "scale" -> scale.toJson())
    val axisOpt = y.axis.map(axis => "axis" -> axis.toJson())

    JsonObject(
      fieldOpt.toSeq ++ typeOpt.toSeq ++ aggregateOpt.toSeq ++ binOpt.toSeq ++ scaleOpt.toSeq ++ axisOpt.toSeq
    )

extension (y2: Y2Class)
  def toJson(): JsonValue =
    val fieldOpt = y2.field.map(field => "field" -> field.toJson())
    val typeOpt = y2.`type`.map(`type` => "type" -> `type`.toJson())
    JsonObject(fieldOpt.toSeq ++ typeOpt.toSeq)

extension (color: ColorClass)
  def toJson(): JsonValue =
    val fieldOpt = color.field.map(field => "field" -> field.toJson())
    val typeOpt = color.`type`.map(`type` => "type" -> `type`.toJson())
    JsonObject(fieldOpt.toSeq ++ typeOpt.toSeq)

extension (size: SizeClass)
  def toJson(): JsonValue =
    val fieldOpt = size.field.map(field => "field" -> field.toJson())
    val typeOpt = size.`type`.map(`type` => "type" -> `type`.toJson())
    JsonObject(fieldOpt.toSeq ++ typeOpt.toSeq)

extension (field: Field)
  def toJson(): JsonValue =
    JsonString(field.toString())

extension (`type`: Type)
  def toJson(): JsonValue =
    JsonString(`type`.toString())

extension (scale: Scale)
  def toJson(): JsonValue =
    val zeroOpt = scale.zero.map(zero => "zero" -> zero.toJson())
    val scaleTypeOpt = scale.`type`.map(`type` => "type" -> `type`.toJson())
    val domainMinOpt =
      scale.domainMin.map(domainMin => "domainMin" -> domainMin.toJson())
    val domainMaxOpt =
      scale.domainMax.map(domainMax => "domainMax" -> domainMax.toJson())
    JsonObject(
      zeroOpt.toSeq ++ domainMinOpt.toSeq ++ domainMaxOpt.toSeq ++ scaleTypeOpt
    )

extension (scaleType: ScaleType)
  def toJson(): JsonValue =
    JsonString(scaleType.toString())

extension (aria: Aria)
  def toJson(): JsonValue =
    aria match
      case b: Boolean => JsonBool(b)
      case _ => throw new Exception("No json encoding for Aria != Boolean")

extension (domainM: DomainM)
  @targetName("domainMToJson")
  def toJson(): JsonValue =
    domainM match
      case d: Double => JsonNumber(d)
      case _ => throw new Exception("No json encoding for DomainM != Double")

extension (aggregate: Aggregate)
  @targetName("toJsonAggregate")
  def toJson(): JsonValue =
    JsonString(aggregate.toString())

extension (bin: BinParams | Boolean | String | NullValue)
  @targetName("toJsonBin")
  def toJson(): JsonValue =
    bin match
      case bin: Boolean => JsonBool(bin)
      case bin: String  => JsonString(bin)
      case _ =>
        throw new Exception(
          "No json encoding for BinParams != Boolean | String"
        )

extension (axis: Axis)
  def toJson(): JsonValue =
    val labelFontSizeOpt = axis.labelFontSize.map(labelFontSize =>
      "labelFontSize" -> labelFontSize.toJson()
    )
    val titleFontSize = axis.titleFontSize.map(titleFontSize =>
      "titleFontSize" -> titleFontSize.toJson()
    )
    JsonObject(labelFontSizeOpt.toSeq ++ titleFontSize.toSeq)

extension (fontSize: FontSize)
  @targetName("toJsonFontSize")
  def toJson(): JsonValue =
    fontSize match
      case f: Double => JsonNumber(f)
      case _ => throw new Exception("No json encoding for FontSize != Double")

extension (gridWithUnion: GridWidthUnion)
  @targetName("toJsonGridWithUnion")
  def toJson(): JsonValue =
    gridWithUnion match
      case f: Double => JsonNumber(f)
      case _ =>
        throw new Exception("No json encoding for GridWithUnion != Double")

///////////////////////////
// Mark
///////////////////////////

extension (mark: AnyMark)
  @targetName("toJsonAnyMark")
  def toJson(): JsonValue =
    mark match
      case mark: String => JsonString(mark)
      case mark: Def    => mark.toJson()

extension (mark: Def)
  @targetName("toJsonMarkDef")
  def toJson(): JsonValue =
    val typeStr = "type" -> JsonString(mark.`type`)
    val clipOpt = mark.clip.map(clipMark => "clip" -> JsonBool(clipMark))
    val opacityOpt = mark.opacity.map(opacity => "opacity" -> opacity.toJson())
    JsonObject(typeStr +: (clipOpt.toSeq ++ opacityOpt.toSeq))

///////////////////////////
// Data
///////////////////////////
extension (data: URLData)
  def toJson(): JsonValue =
    val valuesOpt = data.values.map(values => "values" -> values.toJson())
    JsonObject(valuesOpt.toSeq)

extension (values: InlineDataset)
  def anyValueToJsonValue(value: Any): JsonValue =
    value match
      case v: Double  => JsonNumber(v)
      case v: Int     => JsonNumber(v)
      case v: String  => JsonString(v)
      case v: Boolean => JsonBool(v)
      case v: Map[String, Any] =>
        JsonObject(v.map { case (k, v) => k -> anyValueToJsonValue(v) }.toSeq)
      case v: Seq[Any] => JsonArray(v.map(anyValueToJsonValue))
      case _           => throw new Exception("Unsupported type for value")

  @targetName("toJsonInlineDataset")
  def toJson(): JsonValue =
    values match
      // case seqValues : Seq[Double] => JsonArray(seqValues.map(value => JsonNumber(value)))
      case seqOfMaps: Seq[Map[String, Option[Any]]] =>
        JsonArray(
          seqOfMaps.map(value =>
            JsonObject(value.collect { case (k, Some(v)) =>
              k -> anyValueToJsonValue(v)
            }.toSeq)
          )
        )
      case _ => throw new Exception("Unsupported type for values")

///////////////////////////
// Layer
///////////////////////////

extension (layer: LayerSpec)
  def toJson(): JsonValue =
    val layerOpt = layer.layer.map(layerSpecs =>
      "layer" -> JsonArray(layerSpecs.map(layerSpec => layer.toJson()))
    )
    val encodingOpt =
      layer.encoding.map(encoding => "encoding" -> encoding.toJson())
    val markOpt = layer.mark.map(mark => "mark" -> mark.toJson())
    val dataOpt = layer.data.map(data => "data" -> data.toJson())
    JsonObject(
      layerOpt.toSeq ++ encodingOpt.toSeq ++ markOpt.toSeq ++ dataOpt.toSeq
    )
