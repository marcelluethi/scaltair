package scalismo.plot.plots

import scalismo.plot.vegalite.Data.DataValue

object DataTransforms:
  /** Convert a map of data to a histogram. The value of x indicates the column
    * of the data to use for the histogram. y denotes the value of the column
    * into which the counts are stored. The method returns a new map that
    * contains the histogram data.
    */
  def toHistogram(
      data: Map[String, Seq[DataValue]],
      x: String,
      y: String,
      binCount: Int = 10
  ): Map[String, Seq[DataValue]] =
    val xData = data(x).collect({ case DataValue.Quantitative(v) => v })
    val (minX, maxX) = (xData.min, xData.max)
    val binSize = (maxX - minX) / binCount
    val bins = (0 until binCount).map(i => minX + i * binSize)
    val counts = xData.map(v => bins.indexWhere(b => v < b))
    val binCounts = counts.groupBy(identity).mapValues(_.size)
    val binCountsSeq = bins.map(b => binCounts.getOrElse(bins.indexOf(b), 0))
    Map(
      "x" -> bins.map(v => DataValue.Nominal(v.toInt.toString)),
      y -> binCountsSeq.map(DataValue.Quantitative(_))
    )
