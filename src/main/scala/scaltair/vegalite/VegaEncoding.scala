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

import scaltair.json.JsonObject
import scaltair.json.JsonString
import netscape.javascript.JSObject
import VegaEncoding.{Channel, ChannelProp}
import scaltair.json.JsonValue
import scaltair.json.JsonBool
import scaltair.json.JsonArray
import scaltair.json.JsonNumber

final case class VegaEncoding(channels: Seq[Channel]) extends VegaLite:
  override def spec: JsonObject =
    JsonObject(
      for (channel <- channels) yield
        val props = channel.props
        val channelPropSpecs = for (prop <- props) yield prop.name -> prop.spec

        channel.name -> JsonObject(channelPropSpecs)
    )

object VegaEncoding:

  enum Channel(val name: String, val props: Seq[ChannelProp]):
    case X(
        fieldName: String,
        fieldType: FieldType,
        otherProps: Seq[ChannelProp] = Seq.empty
    ) extends Channel(
          "x",
          otherProps ++ Seq(
            ChannelProp.Field(fieldName),
            ChannelProp.Type(fieldType)
          )
        )
    case Y(
        fieldName: String,
        fieldType: FieldType,
        otherProps: Seq[ChannelProp] = Seq.empty
    ) extends Channel(
          "y",
          otherProps ++ Seq(
            ChannelProp.Field(fieldName),
            ChannelProp.Type(fieldType)
          )
        )
    case Y2(
        fieldName: String,
        fieldType: FieldType,
        otherProps: Seq[ChannelProp] = Seq.empty
    ) extends Channel("y2", Seq(ChannelProp.Field(fieldName)))

    case Color(
        fieldName: String,
        fieldType: FieldType,
        otherProps: Seq[ChannelProp] = Seq.empty
    ) extends Channel(
          "color",
          Seq(
            ChannelProp.Field(fieldName),
            ChannelProp.Type(fieldType)
          ) ++ otherProps
        )
    case Size(fieldName: String, otherProps: Seq[ChannelProp] = Seq.empty)
        extends Channel(
          "size",
          Seq(
            ChannelProp.Field(fieldName),
            ChannelProp.Type(FieldType.Quantitative)
          ) ++ otherProps
        )

  enum ChannelProp(val name: String, val spec: JsonValue):
    case Field(fieldName: String)
        extends ChannelProp("field", JsonString(fieldName))
    case Type(fieldType: FieldType) extends ChannelProp("type", fieldType.spec)
    case Bin(bin: BinConfig) extends ChannelProp("bin", bin.spec)
    case Aggregate(agregateType: AggregateType)
        extends ChannelProp("aggregate", agregateType.spec)
    case Custom(propName: String, customSpec: JsonValue)
        extends ChannelProp(propName, customSpec)
    case Scale(scaleSpec: ScaleSpec)
        extends ChannelProp("scale", scaleSpec.spec)
    case Axis(axisProps: Seq[AxisProp])
        extends ChannelProp(
          "axis",
          JsonObject(axisProps.map(prop => prop.name -> prop.spec))
        )

  enum ScaleSpec(val spec: JsonValue) extends VegaLite:
    case IncludeZero(zero: Boolean)
        extends ScaleSpec(JsonObject(Seq("zero" -> JsonBool(zero))))
    case FromDomain(domain: Domain) extends ScaleSpec(domain.spec)

  enum AxisProp(val name: String, val spec: JsonValue) extends VegaLite:
    case LabelFontSize(fontSize: Int)
        extends AxisProp("labelFontSize", JsonNumber(fontSize))
    case TitleFontSize(fontSize: Int)
        extends AxisProp("titleFontSize", JsonNumber(fontSize))

  enum BinConfig(val spec: JsonValue) extends VegaLite:
    case MaxBins(maxBins: Int)
        extends BinConfig(JsonObject(Seq("maxbins" -> JsonNumber(maxBins))))
    case AutoBin(b: Boolean) extends BinConfig(JsonBool(b))

  enum FieldType(val spec: JsonValue) extends VegaLite:

    case Quantitative extends FieldType(JsonString("quantitative"))
    case Ordinal extends FieldType(JsonString("ordinal"))
    case Nominal extends FieldType(JsonString("nominal"))
    case Temporal extends FieldType(JsonString("temporal"))

  enum AggregateType(val spec: JsonValue) extends VegaLite:

    case Mean extends AggregateType(JsonString("mean"))
    case Max extends AggregateType(JsonString("max"))
    case Count extends AggregateType(JsonString("count"))

  case class Domain(val minValue: Double, maxValue: Double) extends VegaLite:
    override def spec: JsonValue = JsonObject(
      Seq(
        "domain" -> JsonArray(Seq(JsonNumber(minValue), JsonNumber(maxValue)))
      )
    )
