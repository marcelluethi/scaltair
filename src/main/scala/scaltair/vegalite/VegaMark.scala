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
package scaltair.vegalite

import scaltair.json.JsonObject
import scaltair.json.JsonString
import scaltair.json.JsonValue

enum VegaMark(val markSpec: JsonValue) extends VegaLite:
  override def spec: JsonValue = markSpec

  case Line extends VegaMark(JsonString("line"))
  case Bar extends VegaMark(JsonString("bar"))
  case Area extends VegaMark(JsonString("area"))
  case Boxplot extends VegaMark(JsonString("boxplot"))
  case Circle extends VegaMark(JsonString("circle"))
  case ErrorBand extends VegaMark(JsonString("errorband"))
  case ErrorBar extends VegaMark(JsonString("errorbar"))
  case Point extends VegaMark(JsonString("point"))
  case Rect extends VegaMark(JsonString("rect"))
  case Square extends VegaMark(JsonString("square"))
  case Text extends VegaMark(JsonString("text"))
  case Custom(customSpec: JsonValue) extends VegaMark(customSpec)
