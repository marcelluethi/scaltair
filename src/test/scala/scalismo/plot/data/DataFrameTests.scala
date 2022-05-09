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

import java.io.File
import scalismo.plot.data.DataFrame.{DataRow}
import scalismo.plot.data.Column

class DataFrameTests extends munit.FunSuite {

  test("properly reads a csv file") {
    val url = getClass.getResource("/test.csv")
    val df = DataFrame.fromCSV(new File(url.getPath), ",")
    assert(df.isSuccess, "data frame could not be loaded")
    assertEquals(
      df.get.columns.map(_._1).toSeq.sorted,
      Seq("bone-length", "id", "sex", "stature", "trochanter-distance")
    )

  }

  test("can select columns") {
    val url = getClass.getResource("/test.csv")
    val df = DataFrame.fromCSV(new File(url.getPath), ",").get
    val dfRestricted = df.select(Seq("id", "stature"))

    assertEquals(dfRestricted.columns.size, 2)
    val columnTitles = dfRestricted.columns.map(_._1)
    assert(columnTitles.contains("id") && columnTitles.contains("stature"))

  }

  test("a union of two disjoint dataframes contains all columns") {
    val col1 = Column.ofContinuous(Seq(1.0, 2.0, 3.0), "col1")
    val col2 = Column.ofContinuous(Seq(4.0, 5.0, 6.0), "col2")
    val col3 = Column.ofContinuous(Seq(7.0, 8.0, 9.0), "col3")
    val col4 = Column.ofContinuous(Seq(10.0, 11.0, 12), "col4")

    val df1 = DataFrame(Seq(col1, col2))
    val df2 = DataFrame(Seq(col3, col4))

    val df3 = df1.union(df2)
    assertEquals(df3.columns.length, 4)
    assert(df3.columns.map(_._1) == Seq("col1", "col2", "col3", "col4"))
  }

  test("a union of two dataframes contains common columns only once") {
    val col1 = Column.ofContinuous(Seq(1.0, 2.0, 3.0), "col1")
    val col2 = Column.ofContinuous(Seq(4.0, 5.0, 6.0), "col2")
    val col3 = Column.ofContinuous(Seq(5.0, 6.0, 7.0), "col3")
    val col4 = Column.ofContinuous(Seq(8.0, 6.0, 7.0), "col4")

    val df1 = DataFrame(Seq(col1, col2, col3))
    val df2 = DataFrame(Seq(col2, col3, col4))

    val df3 = df1.union(df2)
    assertEquals(df3.columns.length, 4)
    assert(df3.columns.map(_._1) == Seq("col1", "col2", "col3", "col4"))
  }

  test("filtering rows only retains the rows satisfying the predicate") {
    val col1 = Column.ofContinuous(Seq(1.0, 2.0, 3.0), "col1")

    val df = DataFrame(Seq(col1))
    val newDf = df.filter(row =>
      row
        .find(cell => cell.name == "col1")
        .map(_.value.asInstanceOf[Double] > 1.1)
        .getOrElse(false)
    )

    assertEquals(newDf.rows.length, 3)
  }

}
