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
package scaltair

/** A channel is a mapping, which determines how a given field is represented in
  * the plot. For example, a field could be interpreted as representing the
  * x-axis, the y-axis or the color of the plot. Besides many other properties,
  * every channel has a field name and a type.
  */
sealed trait Channel(
    fieldName: String,
    fieldType: FieldType
)

private case class XChannel(
    private[scaltair] val fieldName: String,
    private[scaltair] val fieldType: FieldType,
    private[scaltair] val bins: Option[Bin] = None,
    private[scaltair] val scale: Option[Scale] = Some(
      Scale().axisIncludesZero(false)
    ),
    private[scaltair] val axis: Option[Axis] = None
) extends Channel(fieldName, fieldType):

  def scale(scale: Scale): XChannel =
    XChannel(fieldName, fieldType, bins, Some(scale), axis)

  def axis(axis: Axis): XChannel =
    XChannel(fieldName, fieldType, bins, scale, Some(axis))

  def binned(): XChannel =
    XChannel(fieldName, fieldType, Some(Bin.Auto), scale, axis)

  def binned(properties: BinProperties): XChannel =
    XChannel(
      fieldName,
      fieldType,
      Some(Bin.CustomBinning(properties)),
      scale,
      axis
    )

private case class YChannel(
    private[scaltair] val fieldName: String,
    private[scaltair] val fieldType: FieldType,
    private[scaltair] val agg: Option[AggregateType] = None,
    private[scaltair] val scale: Option[Scale] = Some(
      Scale().axisIncludesZero(false)
    ),
    private[scaltair] val axis: Option[Axis] = None
) extends Channel(fieldName, fieldType):

  def count(): Channel =
    YChannel(
      fieldName,
      fieldType,
      Some(AggregateType.Count),
      scale
    )

  def scale(scale: Scale): YChannel =
    YChannel(fieldName, fieldType, agg, Some(scale))

  def axis(axis: Axis): YChannel =
    YChannel(fieldName, fieldType, agg, scale, Some(axis))

private case class Y2Channel(
    private[scaltair] val fieldName: String,
    private[scaltair] val fieldType: FieldType,
    private[scaltair] val agg: Option[AggregateType] = None
) extends Channel(fieldName, fieldType):

  def count(): Channel =
    Y2Channel(
      fieldName,
      fieldType
    )

  def scale(scale: Scale): Y2Channel =
    Y2Channel(fieldName, fieldType, agg)

  def axis(axis: Axis): Y2Channel =
    Y2Channel(fieldName, fieldType, agg)

private case class ColorChannel(
    private[scaltair] val fieldName: String,
    private[scaltair] val fieldType: FieldType
) extends Channel(fieldName, fieldType)

private case class SizeChannel(private[scaltair] val fieldName: String)
    extends Channel(fieldName, FieldType.Quantitative)

private case class TextChannel(private[scaltair] val fieldName: String)
    extends Channel(fieldName, FieldType.Nominal)

private case class OrderChannel(private[scaltair] val fieldName: String)
    extends Channel(fieldName, FieldType.Ordinal)

object Channel:

  def X(fieldName: String, fieldType: FieldType): XChannel =
    XChannel(fieldName, fieldType)

  def Y(fieldName: String, fieldType: FieldType): YChannel =
    YChannel(fieldName, fieldType)

  def Y2(fieldName: String, fieldType: FieldType): Y2Channel =
    Y2Channel(fieldName, fieldType)

  def Color(fieldName: String, fieldType: FieldType): ColorChannel =
    ColorChannel(fieldName, fieldType)

  def Size(fieldName: String): SizeChannel = SizeChannel(fieldName)

  def Text(fieldName: String): TextChannel = TextChannel(fieldName)

  def Order(fieldName: String): OrderChannel = OrderChannel(fieldName)

enum AggregateType:
  case Count, Valid, Missing, Distinct, Sum, Mean, Average, Variance, VarianceP,
    Stdev, StdevP, Median, Q1, Q3, Modeskew, Min, Max, Argmin, Argmax, Ci0, Ci1,
    Cimin, Cimax, Cistderr, Ciquantile, Cilower, Cihigher, Cilimit, Nthvalue,
    Ntile, Lags, Lead, Lag, Percentrank, CumeDist, FirstValue, LastValue,
    NthValue

private case class Scale(
    private[scaltair] val axisIncludesZero: Boolean,
    private[scaltair] val scaleType: Option[ScaleType],
    private[scaltair] val domain: Option[Domain]
):
  def domain(domain: Domain): Scale =
    this.copy(domain = Some(domain))
  def axisIncludesZero(axisIncludesZero: Boolean): Scale =
    this.copy(axisIncludesZero = axisIncludesZero)
  def scaleType(scaleType: ScaleType): Scale =
    this.copy(scaleType = Some(scaleType))

object Scale:
  def apply(): Scale =
    Scale(axisIncludesZero = false, domain = None, scaleType = None)

case class Domain(min: Double, max: Double)

enum ScaleType:
  case Logarithmic extends ScaleType
  case Linear extends ScaleType

private case class Axis(
    private[scaltair] val labelFontSize: Int,
    private[scaltair] val titleFontSize: Int
):

  def labelFontSize(labelFontSize: Int): Axis =
    this.copy(labelFontSize = labelFontSize)
  def titleFontSize(titleFontSize: Int): Axis =
    this.copy(titleFontSize = titleFontSize)

object Axis:
  def apply(): Axis = Axis(labelFontSize = 14, titleFontSize = 14)

enum Bin:
  case Auto extends Bin
  case CustomBinning(properties: BinProperties) extends Bin

private case class BinProperties(private[scaltair] val maxbins: Option[Int]):
  def maxbins(maxbins: Int): BinProperties =
    this.copy(maxbins = Some(maxbins))

object BinProperties:
  def apply(): BinProperties = BinProperties(maxbins = None)

enum FieldType:
  case Quantitative, Nominal, Ordinal, Temporal
