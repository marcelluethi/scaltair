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
package scaltair.json

sealed trait JsonValue

case class JsonObject(fields: Seq[(String, JsonValue)]) extends JsonValue

case class JsonArray(elements: Seq[JsonValue]) extends JsonValue

case class JsonString(value: String) extends JsonValue

case class JsonNumber(value: Double) extends JsonValue

case class JsonBool(value: Boolean) extends JsonValue

object Json {

  /** Convert a json Value to its string representation.
    */
  def stringify(value: JsonValue): String = {
    value match {
      case JsonObject(fields) => {
        val sb = new StringBuilder
        sb.append("{")
        var first = true
        for ((k, v) <- fields) {
          if (first) {
            first = false
          } else {
            sb.append(",")
          }
          sb.append("\"" + k + "\":" + stringify(v))
        }
        sb.append("}")
        sb.toString()
      }
      case JsonArray(elements) => {
        val sb = new StringBuilder
        sb.append("[")
        var first = true
        for (e <- elements) {
          if (first) {
            first = false
          } else {
            sb.append(",")
          }
          sb.append(stringify(e))
        }
        sb.append("]")
        sb.toString()
      }
      case JsonString(value) => "\"" + value + "\""
      case JsonNumber(value) => value.toString
      case JsonBool(value)   => value.toString
    }
  }
}
