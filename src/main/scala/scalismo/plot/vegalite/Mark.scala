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
import scalismo.plot.json.JsonValue

enum Mark(val markSpec: JsonValue) extends VegaLite:
  override def spec: JsonValue = markSpec

  case Line extends Mark(JsonString("line"))
  case Bar extends Mark(JsonString("bar"))
  case Area extends Mark(JsonString("area"))
  case Boxplot extends Mark(JsonString("boxplot"))
  case Circle extends Mark(JsonString("circle"))
  case ErrorBand extends Mark(JsonString("errorband"))
  case ErrorBar extends Mark(JsonString("errorbar"))
  case Point extends Mark(JsonString("point"))
  case Rect extends Mark(JsonString("rect"))
  case Square extends Mark(JsonString("square"))
  case Text extends Mark(JsonString("text"))
  case Custom(customSpec: JsonValue) extends Mark(customSpec)
