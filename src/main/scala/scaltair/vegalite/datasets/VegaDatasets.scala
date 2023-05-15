package scaltair.vegalite.datasets

import scala.io.Source
import scala.collection.mutable

import java.net.URL
import scala.util.Try
import scaltair.Data.ColumnData
import scaltair.Data
import java.util.IllegalFormatException

object VegaDatasets:

  private def jsonTextToValue(s: String): Any =
    if s.trim().startsWith("\"") then
      s.trim()
        .substring(1, s.length() - 1) // remove leading and training quotes
    else if s.toIntOption.isDefined then s.toInt
    else if s.toDoubleOption.isDefined then s.toDouble
    else
      throw new IllegalArgumentException(s"cannot convert $s to a valid value")

  private def csvTextToValue(s: String): Any =
    if s.toIntOption.isDefined then s.toInt
    else if s.toDoubleOption.isDefined then s.toDouble
    else s.trim()

  private def fromCSV(url: URL, separator: String = ","): Try[ColumnData] =
    Try {
      val lines = scala.io.Source.fromURL(url).getLines().toList
      val header = lines.head.split(separator).toSeq
      val rows = for line <- lines.tail yield
        val lineValues =
          line
            .split(separator)
            .map(cellValueStr => csvTextToValue(cellValueStr))
        header.zip(lineValues).toMap

      Data.fromRows(rows)
    }

  private def fromJson(url: URL): Try[ColumnData] = Try {

    // as we do not want to add a json parser as a dependency,
    // we just extract the raw data from the json files using
    // string matching. It is hacky and not robust but the datasets
    // should not change much and hence it is okay.
    //
    val text = Source.fromURL(url).getLines().mkString("\n")
    val blockPattern = "\\{([^}]*)\\}".r
    val nullPattern = ":(\\s*)null".r
    val blocks = blockPattern
      .findAllMatchIn(text)
      .map(_.group(1))
      .filterNot(block =>
        nullPattern.findFirstIn(block).isDefined
      ) // filter all blocks with null values
      .toIndexedSeq

    val rows = for block <- blocks yield
      val lines = block.split("\n").filter(l => l.trim != "")
      val fields = for line <- lines yield
        val fields = line.split(":")
        val key = fields.head.trim().replace("\"", "")
        val rawValue = fields.last.trim()
        val value =
          if rawValue.endsWith(",") then
            jsonTextToValue(rawValue.substring(0, rawValue.length() - 1))
          else jsonTextToValue(rawValue)

        (key, value)
      fields.toMap
    Data.fromRows(rows)
  }

  def loadCars() = fromJson(
    URL(
      "https://raw.githubusercontent.com/vega/vega-datasets/main/data/cars.json"
    )
  )
  def loadWeather() = fromCSV(
    URL(
      "https://raw.githubusercontent.com/vega/vega-datasets/main/data/weather.csv"
    )
  )
