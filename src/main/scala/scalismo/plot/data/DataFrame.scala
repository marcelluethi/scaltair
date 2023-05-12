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

import DataFrame.{DataCell, Column}
import scala.util.Try
import javax.xml.crypto.Data
import scalismo.plot.ScalismoPlot
import scala.collection.mutable
import scala.io.Codec
import java.nio.file.Path
import scalismo.plot.data.DataFrame.CellValue

trait DataFrameOps[A <: DataFrameOps[A]]:

  def columns: Seq[Column]

  def fromColumns(seq: Seq[Column]): A

  /** Returns a new dataframe consisting of the selected columns.
    */
  def select(columnNames: Seq[String]): A =
    val selectedColumns =
      columnNames
        .map(colName => columns.find((c: Column) => c.name == colName))
        .flatten
    fromColumns(selectedColumns)

  /** Returns a new dataframe, which is the union of the columns in this
    * dataframe and the dataframe passed as an argument.
    */
  def union(other: A): A =
    require(
      columns.forall(col => col._2.length == columns.head._2.length),
      "all columns have the same number of elements"
    )

    val sameColumns = columns.map(_._1).intersect(other.columns.map(_._1))

    fromColumns(
      columns ++ other.columns.filterNot(col => sameColumns.contains(col._1))
    )

  /** Returns the column with the given name
    */
  def column(name: String): Column =
    columns.find(col => col.name == name).get

  /** Returns the element at the given row and column
    */
  def at(row: Int, columnName: String): CellValue =
    column(columnName).values(row)

  /** A printout of the dataframe with the fields aligned in columns
    */
  def formatAsString(rows: Seq[DataRow]): String =
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
      lines.append(rowFormat.format(row.toSeq.map(_.value): _*))
    }
    lines.mkString("\n")

final case class DataRow(columns: Seq[DataFrame.Column])
    extends DataFrameOps[DataRow]:

  def fromColumns(seq: Seq[Column]): DataRow =
    require(
      seq.forall(_.values.length == 1),
      "all columns have exactly one element"
    )
    DataRow(seq)

  def apply(columns: Range = Range(0, columns.length)): DataRow =
    DataRow(this.columns.slice(columns.start, columns.end))

  def apply(columnName: String): CellValue = select(
    Seq(columnName)
  ).columns.head.values.head

  def apply(index: Int): DataCell =
    val col = columns(index)
    DataCell(col.name, col.values.head)

  def length: Int = columns.length

  def map(f: DataCell => CellValue): DataRow =
    DataRow(
      columns.map(col =>
        Column(col.name, col.values.map(value => f(DataCell(col.name, value))))
      )
    )

  def toSeq: Seq[DataCell] =
    columns.map(col => DataCell(col.name, col.values.head))

  def toMap: Map[String, CellValue] =
    columns.map(col => col.name -> col.values.head).toMap

  override def toString(): String =
    formatAsString(Seq(this))

/** A labelled sequence of columns of data.
  */
final case class DataFrame(columns: Seq[DataFrame.Column])
    extends DataFrameOps[DataFrame]:

  override def fromColumns(columns: Seq[Column]): DataFrame = DataFrame(columns)

  /** Returns the number of rows in this data frame
    */
  def numberOfRows: Int = columns.head.values.length

  /** Returns a dataframe with the rows and columns in the given range
    */
  def apply(
      rows: Range = Range(0, rows.length),
      columns: Range = Range(0, columns.length)
  ): DataFrame =
    val dfWithSelectedColumns = DataFrame(
      this.columns.slice(columns.start, columns.end)
    )
    DataFrame.fromRows(dfWithSelectedColumns.rows.slice(rows.start, rows.end))

  /** Returns the rows of the dataframe
    */
  def rows: Seq[DataRow] =

    require(
      columns.forall(col => col._2.length == columns.head._2.length),
      "all columns have the same number of elements"
    )

    val nRows = columns.head.values.length

    for (i <- 0 until nRows) yield
      val rowData =
        for (DataFrame.Column(colId, data) <- columns)
          yield DataFrame.Column(colId, Seq(data(i)))
      DataRow(rowData)

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

  /** Appends a new column to the dataframe
    */
  def appendColumn(column: Column): DataFrame =
    DataFrame(columns :+ column)

  /** Returns a new dataframe consisting of only the rows whose predicate is
    * true.
    */
  def filter(predicate: DataRow => Boolean): DataFrame =

    val filteredRows =
      for row <- rows yield if predicate(row) then Some(row) else None
    DataFrame.fromRows(filteredRows.flatten)

  def map(f: DataRow => DataRow): DataFrame =
    DataFrame.fromRows(rows.map(f))

  override def toString(): String =
    formatAsString(rows)

  def plot: ScalismoPlot = ScalismoPlot(this)

object DataFrame:

  /** Creates a dataframe from the given csv file
    */
  def fromCSV(path: Path, separator: String)(using
      codec: Codec
  ): Try[DataFrame] =
    fromCSV(path.toFile, separator)

  /** Creates a dataframe from the given csv file
    */
  def fromCSV(file: java.io.File, separator: String = ",")(using
      codec: Codec
  ): Try[DataFrame] =
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

    // contains the colums that are found in rows as key
    // and the corresponding value is the sequence of all values found in the rows
    val columnMap = mutable.Map[String, Seq[CellValue]]()

    for (row <- rows) do
      for (col <- row.columns) do
        columnMap.get(col.name) match
          case Some(values) => columnMap(col.name) = values :+ col.values.head
          case None         => columnMap(col.name) = Seq(col.values.head)

    // we need to make sure that the columns are in the same order as in the first row
    // Therefore we use the columns of the first row as a reference
    val firstRow = rows.head
    assert(
      firstRow.columns.map(_.name).toSet == columnMap.keys,
      "all columns must be present in all rows"
    )

    val data =
      firstRow.columns.map(col => Column(col.name, columnMap(col.name))).toSeq
    DataFrame(data)

  /** Constructs a dataframe from the given columns
    */
  def fromColumns(columns: Seq[Column]): DataFrame =
    DataFrame(columns)

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

    def asContinuous: CellValue.Continuous = asContinuousOpt match
      case Some(c) => c
      case None =>
        throw new Exception(s"cannot convert $value to continuous value")

    def asContinuousOpt: Option[CellValue.Continuous] = this match
      case c @ CellValue.Continuous(d) => Some(c)
      case CellValue.Discrete(d)       => Some(CellValue.Continuous(d.toDouble))
      case _                           => None

    def asDiscreteOpt: Option[CellValue.Discrete] = this match
      case d @ CellValue.Discrete(i) => Some(d)
      case _                         => None

    def asDiscrete: CellValue.Discrete = asDiscreteOpt match
      case Some(d) => d
      case _ => throw new Exception(s"cannot convert $value to discrete value")

    def asNominalOpt: Option[CellValue.Nominal] = this match
      case n @ CellValue.Nominal(s) => Some(n)
      case _                        => None

    def asNominal: CellValue.Nominal = asNominalOpt match
      case Some(n) => n
      case None =>
        throw new Exception(s"cannot convert $value to nominal value")

    override def toString = value.toString

  object CellValue:
    def fromString(value: String): CellValue =
      value.toIntOption match
        case Some(intValue) => Discrete(intValue)
        case None =>
          value.toDoubleOption match
            case Some(doubleValue) => Continuous(doubleValue)
            case None              => Nominal(value)
