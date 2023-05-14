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

import scalismo.plot.Chart
import scalismo.plot.data.DataFrame
import scalismo.plot.data.DataFrame.*

import scalismo.plot.Channel
import scalismo.plot.Scale
import scalismo.plot.ChartWithViews
import scalismo.plot.CompleteChart
import scalismo.plot.Axis
import scalismo.plot.vegalite.VegaChart
import scalismo.plot.Data.ColumnData

/** The high-level API for creating plots. The data is represented as a
  * DataFrame and passed when constructing the class. The data for the
  * individual plot methods is provided by references to the columns in the
  * DataFrame.
  */
class ScalismoPlot(dataFrame: DataFrame) {

  val data = dataFrame.columns
    .map(column => column.name -> column.values.map(_.value))
    .toMap

  val defaultWidth = 800
  val defaultHeight = 600

  /** create a simple line plot
    */
  def linePlot(
      x: String,
      y: String,
      title: String,
      series: String = "",
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =
    if series.isEmpty then
      Chart(data)
        .encode(
          Channel.X(x, FieldType.Quantitative),
          Channel.Y(y, FieldType.Quantitative)
        )
        .markLine()
        .properties(
          ChartProperties(title = title, width = width, height = height)
        )
    else
      Chart(data)
        .encode(
          Channel.X(x, FieldType.Quantitative),
          Channel.Y(y, FieldType.Quantitative),
          Channel.Color(series)
        )
        .markLine()
        .properties(
          ChartProperties(title = title, width = width, height = height)
        )

  /** Create a line plot with an error band.
    */
  def linePlotWithErrorBand(
      x: String,
      y: String,
      lowerBand: String,
      upperBand: String,
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =
    val lineView = Chart(data)
      .encode(
        Channel.X(x, FieldType.Quantitative),
        Channel.Y(y, FieldType.Quantitative)
      )
      .markLine()

    val errorView = Chart(data)
      .encode(
        Channel.X(x, FieldType.Quantitative),
        Channel.Y(lowerBand, FieldType.Quantitative),
        Channel.Y2(upperBand, FieldType.Quantitative)
      )
      .markErrorBand()

    lineView
      .overlay(errorView)
      .properties(
        ChartProperties(title = title, width = width, height = height)
      )

  /** Create a scatter plot.
    */
  def scatterPlot(
      x: String,
      y: String,
      title: String,
      colorField: String = "",
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =
    if colorField.isEmpty() then
      Chart(data)
        .encode(
          Channel.X(x, FieldType.Quantitative),
          Channel.Y(y, FieldType.Quantitative)
        )
        .markCircle()
        .properties(ChartProperties(title = title))
    else
      Chart(data)
        .encode(
          Channel.X(x, FieldType.Quantitative),
          Channel.Y(y, FieldType.Quantitative),
          Channel.Color(colorField)
        )
        .markCircle()
        .properties(ChartProperties(title = title))

  /** Creates a boxplot
    */
  def boxplot(
      series: String,
      values: String,
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =
    Chart(data)
      .encode(
        Channel.X(series, FieldType.Nominal),
        Channel.Y(values, FieldType.Quantitative)
      )
      .markBoxplot()
      .properties(ChartProperties(title = title))

  /** Creates a trace plot from the given values. A trace plot is simply a line
    * plot, where the x-axis is the index of the values in the given array.
    */
  def tracePlot(
      values: String,
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) = {

    val traceValues = data(values)
    val iterations =
      Seq.range(0, traceValues.length)

    val fullData: ColumnData = Map(
      "Iteration" -> iterations,
      values -> data(values)
    )
    Chart(fullData)
      .encode(
        Channel.X("Iteration", FieldType.Ordinal),
        Channel
          .Y(values, FieldType.Quantitative)
          .scale(Scale(axisIncludesZero = false))
      )
      .markLine()
      .properties(ChartProperties(title = title))

  }

  /** Creates a histogram plot.
    */
  def histogram(
      x: String,
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =
    Chart(data)
      .encode(
        Channel.X(x, FieldType.Quantitative).binned(),
        Channel.Y(x, FieldType.Quantitative).count()
      )
      .markBar()
      .properties(ChartProperties(title = title))

  /** Creates a pair plot from the given columns.
    */
  def pairPlot(
      columnNames: Seq[String],
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ): VegaChart =

    val rows = for (keyR <- columnNames) yield
      val cols =
        for (keyV <- columnNames)
          yield
            if keyR == keyV then
              Chart(data)
                .encode(
                  Channel.X(keyR, FieldType.Quantitative).binned(),
                  Channel.Y(keyV, FieldType.Quantitative).count()
                )
                .markBar()
            else
              Chart(data)
                .encode(
                  Channel.X(keyR, FieldType.Quantitative),
                  Channel.Y(keyV, FieldType.Quantitative)
                )
                .markCircle()
      cols.reduce[CompleteChart]((a, b) => a.hConcat(b))

    val chart = rows
      .reduce((a, b) => a.vConcat(b))

    chart.vegaSpec
}
