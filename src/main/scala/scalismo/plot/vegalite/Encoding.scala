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

import scalismo.plot.json.JsonObject
import scalismo.plot.json.JsonString
import netscape.javascript.JSObject
import Encoding.{Channel, ChannelProp}
import scalismo.plot.json.JsonValue
import scalismo.plot.json.JsonBool
import scalismo.plot.json.JsonArray
import scalismo.plot.json.JsonNumber

final case class Encoding(channels: Seq[Channel]) extends VegaLite:
  override def spec: JsonObject =
    JsonObject(
      for (channel <- channels) yield
        val props = channel.props
        val channelPropSpecs = for (prop <- props) yield prop.name -> prop.spec

        channel.name -> JsonObject(channelPropSpecs)
    )

object Encoding:

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

    case Color(fieldName: String)
        extends Channel("color", Seq(ChannelProp.Field(fieldName)))

  enum ChannelProp(val name: String, val spec: JsonValue):
    case Field(fieldName: String)
        extends ChannelProp("field", JsonString(fieldName))
    case Type(fieldType: FieldType) extends ChannelProp("type", fieldType.spec)
    case Bin(isBinned: Boolean) extends ChannelProp("bin", JsonBool(isBinned))
    case Aggregate(agregateType: AggregateType)
        extends ChannelProp("aggregate", agregateType.spec)
    case Custom(propName: String, customSpec: JsonValue)
        extends ChannelProp(propName, customSpec)
    case Scale(scaleSpec: ScaleSpec)
        extends ChannelProp("scale", scaleSpec.spec)

  enum ScaleSpec(val spec: JsonValue) extends VegaLite:
    case IncludeZero(zero: Boolean)
        extends ScaleSpec(JsonObject(Seq("zero" -> JsonBool(zero))))
    case FromDomain(domain: Domain) extends ScaleSpec(domain.spec)

  enum FieldType(val spec: JsonValue) extends VegaLite:

    case Quantitative extends FieldType(JsonString("quantitative"))
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
