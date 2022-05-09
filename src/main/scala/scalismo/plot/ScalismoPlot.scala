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
package scalismo.plot

import scalismo.plot.vegalite.Encoding
import scalismo.plot.vegalite.Encoding.Channel
import scalismo.plot.vegalite.Encoding.FieldType
import scalismo.plot.vegalite.SingleView
import scalismo.plot.vegalite.Mark
import scalismo.plot.vegalite.Chart
import scalismo.plot.vegalite.Encoding.*
import scalismo.plot.vegalite.View
import scalismo.plot.optics.Lens
import scalismo.plot.json.JsonString
import scalismo.plot.json.JsonValue
import scalismo.plot.plottarget.PlotTargetBrowser
import scalismo.plot.vegalite.HConcatViews
import scalismo.plot.vegalite.VConcatViews
import scala.util.Random
import scalismo.plot.vegalite.LayeredView
import scalismo.plot.data.*
import scalismo.plot.data.DataFrame.*

/** The high-level API for creating plots. The data is represented as a
  * DataFrame and passed when constructing the class. The data for the
  * individual plot methods is provided by references to the columns in the
  * DataFrame.
  */
class ScalismoPlot(data: DataFrame) {

  val defaultWidth = 800
  val defaultHeight = 600

  /** create a simple line plot
    */
  def linePlot(
      xFieldName: String,
      yFieldName: String,
      title: String,
      seriesName: String = "",
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) = linePlotImpl(
    xFieldName,
    yFieldName,
    title,
    seriesName,
    errorBand = None,
    width,
    height
  )

  /** Create a line plot with an error band.
    */
  def linePlotWithErrorBand(
      xFieldName: String,
      yFieldName: String,
      lowerBandFieldName: String,
      upperBandFieldName: String,
      title: String,
      seriesName: String = "",
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) = linePlotImpl(
    xFieldName,
    yFieldName,
    title,
    seriesName,
    errorBand = Some((lowerBandFieldName, upperBandFieldName)),
    width,
    height
  )

  private def linePlotImpl(
      xFieldName: String,
      yFieldName: String,
      title: String,
      seriesName: String = "",
      errorBand: Option[(String, String)] = None,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =

    val xChannel = Channel.X(xFieldName, FieldType.Quantitative)
    val yChannel = Channel.Y(yFieldName, FieldType.Quantitative)
    val encodingLines =
      if !seriesName.isEmpty then
        val colorChannel = Channel.Color(seriesName)
        Encoding(Seq(xChannel, yChannel, colorChannel))
      else Encoding(Seq(xChannel, yChannel))

    val lineView = SingleView(Mark.Line, encodingLines)

    val view = errorBand match
      case Some((lowerFieldName, upperFieldName)) =>
        val encodingErrorBands = Encoding(
          Seq(
            xChannel,
            Channel.Y(lowerFieldName, FieldType.Quantitative),
            Channel.Y2(upperFieldName, FieldType.Quantitative)
          )
        )
        val errorView = SingleView(Mark.ErrorBand, encodingErrorBands)
        LayeredView(Seq(lineView, errorView))
      case None => lineView

    Chart(data, view, title, width, height)

  /** Create a scatter plot.
    */
  def scatterPlot(
      xFieldName: String,
      yFieldName: String,
      title: String,
      colorField: String = "",
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =

    val xChannel = Channel.X(
      xFieldName,
      FieldType.Quantitative,
      Seq(ChannelProp.Scale(ScaleSpec.IncludeZero(false)))
    )
    val yChannel = Channel.Y(
      yFieldName,
      FieldType.Quantitative,
      Seq(ChannelProp.Scale(ScaleSpec.IncludeZero(false)))
    )
    val encoding =
      if !colorField.isEmpty then
        val colorChannel = Channel.Color(colorField)
        Encoding(Seq(xChannel, yChannel, colorChannel))
      else Encoding(Seq(xChannel, yChannel))

    val view = SingleView(Mark.Circle, encoding)
    Chart(data, view, title, width, height)

  /** Creates a boxplot
    */
  def boxplot(
      seriesName: String,
      valuesName: String,
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =
    val xChannel = Channel.X(seriesName, FieldType.Nominal)
    val yChannel = Channel.Y(
      valuesName,
      FieldType.Quantitative,
      Seq(ChannelProp.Scale(ScaleSpec.IncludeZero(false)))
    )
    val encoding = Encoding(Seq(xChannel, yChannel))

    val view = SingleView(Mark.Boxplot, encoding)
    Chart(data, view, title, width, height)

  /** Creates a trace plot from the given values. A trace plot is simply a line
    * plot, where the x-axis is the index of the values in the given array.
    */
  def tracePlot(
      valuesFieldName: String,
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) = {

    val values = data.column(valuesFieldName).values
    val iterations =
      Column.ofDiscretes(Seq.range(0, values.length), "Iteration")

    val fullData = DataFrame(Seq(iterations)).union(data)

    val xChannel = Channel.X("Iteration", FieldType.Quantitative)
    val yChannel = Channel.Y(valuesFieldName, FieldType.Quantitative)
    val encoding = Encoding(Seq(xChannel, yChannel))

    val view = SingleView(Mark.Line, encoding)
    Chart(fullData, view, title, width, height)

  }

  /** Creates a histogram plot.
    */
  def histogram(
      xFieldName: String,
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =

    val encoding = Encoding(
      Seq(
        Channel.X(
          xFieldName,
          FieldType.Quantitative,
          Seq(ChannelProp.Bin(true))
        ),
        Channel.Y(
          xFieldName,
          FieldType.Quantitative,
          Seq(ChannelProp.Aggregate(AggregateType.Count))
        )
      )
    )

    val view = SingleView(Mark.Bar, encoding)
    Chart(data, view, title, width, height)

  /** Creates a pair plot from the given columns.
    */
  def pairPlot(
      columnNames: Seq[String],
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ): Chart =
    val rows = for (keyR <- columnNames) yield
      val cols =
        for (keyV <- columnNames)
          yield
            if keyR == keyV then
              val xChannel = Channel.X(
                keyR,
                FieldType.Quantitative,
                Seq(ChannelProp.Bin(true))
              )
              val yChannel = Channel.Y(
                keyV,
                FieldType.Quantitative,
                Seq(ChannelProp.Aggregate(AggregateType.Count))
              )
              val encoding = Encoding(Seq(xChannel, yChannel))
              SingleView(Mark.Bar, encoding)
            else
              val xChannel = Channel.X(
                keyR,
                FieldType.Quantitative,
                Seq(ChannelProp.Scale(ScaleSpec.IncludeZero(false)))
              )
              val yChannel = Channel.Y(
                keyV,
                FieldType.Quantitative,
                Seq(ChannelProp.Scale(ScaleSpec.IncludeZero(false)))
              )
              val encoding = Encoding(Seq(xChannel, yChannel))
              SingleView(Mark.Circle, encoding)

      HConcatViews(cols.toSeq)
    val view = VConcatViews(rows.toSeq)
    Chart(data, view)

}
