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

import scalismo.plot.plots.Plot
import scalismo.plot.data.DataFrame
import scalismo.plot.data.DataFrame.*

import scalismo.plot.plots.Channel
import scalismo.plot.plots.Scale
import scalismo.plot.plots.PlotWithViews
import scalismo.plot.plots.CompletePlot
import scalismo.plot.plots.Axis
import scalismo.plot.vegalite.VegaChart

/** The high-level API for creating plots. The data is represented as a
  * DataFrame and passed when constructing the class. The data for the
  * individual plot methods is provided by references to the columns in the
  * DataFrame.
  */
class ScalismoPlot(dataFrame: DataFrame) {

  val data = dataFrame.columns
    .map(column =>
      column.name -> column.values.map {
        case CellValue.Continuous(value) => DataValue.Quantitative(value)
        case CellValue.Discrete(value)   => DataValue.Quantitative(value)
        case CellValue.Nominal(value)    => DataValue.Nominal(value)
      }
    )
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
      Plot(data)
        .encode(Channel.X(x), Channel.Y(y))
        .line()
        .chart(title = title, width = width, height = height)
    else
      Plot(data)
        .encode(Channel.X(x), Channel.Y(y), Channel.Color(series))
        .line()
        .chart(title = title, width = width, height = height)

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
    val lineView = Plot(data)
      .encode(Channel.X(x), Channel.Y(y))
      .line()

    val errorView = Plot(data)
      .encode(Channel.X(x), Channel.Y(lowerBand), Channel.Y2(upperBand))
      .errorBand()

    lineView.overlay(errorView).chart(title, width, height)

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
      Plot(data)
        .encode(Channel.X(x), Channel.Y(y))
        .circle()
        .chart(title = title)
    else
      Plot(data)
        .encode(Channel.X(x), Channel.Y(y), Channel.Color(colorField))
        .circle()
        .chart(title = title)

  /** Creates a boxplot
    */
  def boxplot(
      series: String,
      values: String,
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =
    Plot(data)
      .encode(Channel.X(series), Channel.Y(values))
      .boxplot()
      .chart(title = title)

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
      Seq.range(0, traceValues.length).map(i => DataValue.Quantitative(i))

    val fullData: Map[String, Seq[DataValue]] = Map(
      "Iteration" -> iterations,
      values -> data(values)
    )
    Plot(fullData)
      .encode(
        Channel.X("Iteration"),
        Channel.Y(values).scale(Scale.includeZero(false))
      )
      .line()
      .chart(title = title)

  }

  /** Creates a histogram plot.
    */
  def histogram(
      x: String,
      title: String,
      width: Int = defaultWidth,
      height: Int = defaultHeight
  ) =
    Plot(data)
      .encode(Channel.X(x).binned(), Channel.Y(x).count())
      .bar()
      .chart(title = title)

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
              Plot(data)
                .encode(Channel.X(keyR).binned(), Channel.Y(keyV).count())
                .bar()
            else Plot(data).encode(Channel.X(keyR), Channel.Y(keyV)).circle()
      cols.reduce[CompletePlot]((a, b) => a.hConcat(b))

    rows
      .reduce((a, b) => a.vConcat(b))
      .chart(title = title, width = width, height = height)

}
