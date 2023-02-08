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
import scalismo.plot.data.*
import scalismo.plot.data.DataFrame.*

final case class Data(data: DataFrame) extends VegaLite:
  override def spec: JsonObject =
    val dataJson =
      for row <- data.rows yield
        val fieldsJson = for field <- row.toSeq yield
          val DataCell(fieldId, fieldValue) = field
          fieldId -> JsonString(fieldValue.toString)
        JsonObject(fieldsJson)
    JsonObject(Seq("values" -> JsonArray(dataJson)))
