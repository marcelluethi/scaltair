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
import scaltair.json.JsonArray
import scaltair.json.JsonValue
import scaltair.json.JsonNumber

case class VegaTitle(text: String, props: Seq[TitleProp] = Seq.empty)
    extends VegaLite:
  override def spec: JsonObject =
    JsonObject(
      Seq("text" -> JsonString(text)) ++ props.map(prop =>
        prop.name -> prop.spec
      )
    )

enum TitleProp(val name: String, val spec: JsonValue):
  case FontSize(size: Int) extends TitleProp("fontSize", JsonNumber(size))
