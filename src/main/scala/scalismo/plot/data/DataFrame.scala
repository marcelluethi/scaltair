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
package scalismo.plot.data

import DataFrame.{DataRow, DataCell, Column}
import scala.util.Try
import javax.xml.crypto.Data
import scalismo.plot.ScalismoPlot
import scala.collection.mutable

/** A labelled sequence of columns of data.
  */
final case class DataFrame(columns: Seq[Column]):

  /** Returns the number of rows in this data frame
    */
  def numberOfRows: Int = columns.head.values.length

  /** 
   * Returns a dataframe with the rows and columns in the given range
   */
  def apply(rows : Range = Range(0, rows.length), columns : Range = Range(0, columns.length)) : DataFrame =   
    val dfWithSelectedColumns = DataFrame(this.columns.slice(columns.start, columns.end))
    DataFrame.fromRows(dfWithSelectedColumns.rows.slice(rows.start, rows.end))

  /** Returns the number of rows in the data frame.
    */
  def rows: Seq[DataRow] =

    require(
      columns.forall(col => col._2.length == columns.head._2.length),
      "all columns have the same number of elements"
    )

    val nRows = columns.head.values.length
    val rowData =
      for (i <- 0 until nRows)
        yield for (Column(colId, data) <- columns)
          yield DataCell(colId, data(i))
    rowData

  /** Returns a new dataframe consisting of the selected columns.
    */
  def select(columnNames: Seq[String]): DataFrame =
    val selectedColumns =
      columnNames
        .map(colName => columns.find((c: Column) => c.name == colName))
        .flatten
    DataFrame(selectedColumns)

  /** Returns a new dataframe, which is the union of the columns in this
    * dataframe and the dataframe passed as an argument.
    */
  def union(other: DataFrame): DataFrame =
    require(
      columns.forall(col => col._2.length == columns.head._2.length),
      "all columns have the same number of elements"
    )

    val sameColumns = columns.map(_._1).intersect(other.columns.map(_._1))

    DataFrame(
      columns ++ other.columns.filterNot(col => sameColumns.contains(col._1))
    )

  /** Returns a new dataframe with the rows of this dataframe and the one passed
    * as arguments concatenated. It is required that both dataframes have
    * exactly the same columns.
    */
  def concat(other: DataFrame): DataFrame =
    require(
      columns.length == other.columns.length && columns
        .zip(other.columns)
        .forall((c1, c2) => c1.name == c2.name),
      "columns must be the same"
    )
    DataFrame.fromRows(rows ++ other.rows)

  /** Returns a new dataframe consisting of only the rows whose predicate is
    * true.
    */
  def filter(predicate: DataRow => Boolean): DataFrame =
    DataFrame.fromRows(rows.filter(predicate))

  /** Returns the column with the given name
    */
  def column(name: String): Column =
    columns.find(col => col.name == name).get

  /** A printout of the dataframe with the fields aligned in columsn
    */
  override def toString: String =
    val lines = mutable.ListBuffer.empty[String]
    val columnTitles = columns.map(_.name)
    val columnWidths = columns.map(col =>
      Math.max(col.values.map(_.toString.length).max, col.name.length)
    )
    val columnSeparator = "|"
    val rowSeparator = "+" + columnWidths.map(w => "-" * w).mkString("+") + "+"
    val rowFormat = columnWidths.zipWithIndex
      .map { case (w, i) => s"%${w}s" }
      .mkString("|", "|", "|")
    lines.append(rowSeparator)
    lines.append(rowFormat.format(columnTitles: _*))
    lines.append(rowSeparator)
    for (row <- rows) {
      lines.append(rowFormat.format(row.map(_.value).toArray: _*))
    }
    lines.mkString("\n")

  def plot: ScalismoPlot = ScalismoPlot(this)

object DataFrame:
  type DataRow = Seq[DataCell]

  /** Creates a dataframe from the given csv file
    */
  def fromCSV(file: java.io.File, separator: String = ","): Try[DataFrame] =
    Try {
      val lines = scala.io.Source.fromFile(file).getLines().toList
      val header = lines.head.split(separator).toSeq
      val data = lines.tail
        .map(line =>
          line
            .split(separator)
            .map(cellValueStr => CellValue.fromString(cellValueStr.trim()))
        )
      val columns =
        for (header, colValues) <- header.zip(data.transpose)
        yield Column(header, colValues)

      DataFrame(columns)
    }

  /** Constructs a dataframe from the given rows
    */
  def fromRows(rows: Seq[DataRow]): DataFrame =
    val firstRow = rows.headOption.getOrElse(Seq.empty)
    val labels = firstRow.map(_.name)

    val data = for (colNum <- 0 until firstRow.length) yield
      val colValues = for (row <- rows) yield row(colNum)._2
      Column(firstRow(colNum)._1, colValues)

    DataFrame(data)

  /** Represents a column of a data frame
    */
  case class Column(name: String, values: Seq[CellValue]):
    def rename(newName: String): Column =
      Column(newName, values)

    def numberOfEntries: Int = values.length

  object Column:
    def ofContinuous[A](seq: Seq[Double], name: String): Column =
      Column(name, seq.map(v => CellValue.Continuous(v)))
    def ofDiscretes(seq: Seq[Int], name: String): Column =
      Column(name, seq.map(v => CellValue.Discrete(v)))
    def ofNominals(seq: Seq[String], name: String): Column =
      Column(name, seq.map(v => CellValue.Nominal(v)))

  case class DataCell(name: String, value: CellValue)

  enum CellValue(value: Any):
    case Nominal(value: String) extends CellValue(value)
    case Discrete(value: Int) extends CellValue(value)
    case Continuous(value: Double) extends CellValue(value)

    override def toString = value.toString

  object CellValue:
    def fromString(value: String): CellValue =
      value.toIntOption match
        case Some(intValue) => Discrete(intValue)
        case None =>
          value.toDoubleOption match
            case Some(doubleValue) => Continuous(doubleValue)
            case None              => Nominal(value)
