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
import scalismo.plot.json.JsonNumber
import scalismo.plot.json.JsonArray

sealed trait View

case class SingleView(mark: Mark, encoding: Encoding) extends View:

  def addLayer(view: SingleView): LayeredView =
    new LayeredView(Seq(this, view))

sealed trait CompositeView extends View:
  def views: Seq[View]

case class LayeredView(val views: Seq[SingleView]) extends CompositeView

case class HConcatViews(val views: Seq[View]) extends CompositeView

case class VConcatViews(val views: Seq[View]) extends CompositeView
