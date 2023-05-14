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

import scala.reflect.internal.util.TableDef.Column

object Data:

  type ColumnName = String

  /** A map from column names to the data values in the column.
    */
  type ColumnData = Map[ColumnName, Seq[Any]]

  /** Given a sequence of rows, each represented as a map from column names to
    * the values in the row, return a map from column names to the data values
    */
  def fromRows(rows: Seq[Map[ColumnName, Seq[Any]]]): ColumnData =
    rows.foldLeft(Map.empty[ColumnName, Seq[Any]]) { (acc, row) =>
      acc ++ row.map { case (column, values) =>
        column -> (acc.getOrElse(column, Seq.empty[Any]) ++ values)
      }
    }
