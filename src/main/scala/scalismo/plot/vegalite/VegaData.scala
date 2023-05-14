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
package scalismo.plot.vegalite

import scalismo.plot.json.JsonObject
import scalismo.plot.json.JsonString
import scalismo.plot.json.JsonArray
import scalismo.plot.json.JsonValue
import scalismo.plot.json.JsonNumber
import scalismo.plot.Data.ColumnData

final case class VegaData(data: ColumnData) extends VegaLite:
  require(
    data.values.forall(_.size == data.values.head.size),
    "All data fields must have the same size"
  )
  val numRows = data.head._2.size
  override def spec: JsonObject =
    val dataJson =
      for row <- 0 until numRows yield
        val fieldsJson = for fieldId <- data.keys yield
          val value = data(fieldId)(row)
          val valueJson = value match
            case value: String => JsonString(value)
            case value: Double => JsonNumber(value)
            case value: Int    => JsonNumber(value)
            case value: Short  => JsonNumber(value)
            case value: Long   => JsonNumber(value.toDouble)
            case _ =>
              throw new IllegalArgumentException(
                s"Unsupported data type: $value"
              )
          fieldId -> valueJson
        JsonObject(fieldsJson.toSeq)
    JsonObject(Seq("values" -> JsonArray(dataJson.toSeq)))
