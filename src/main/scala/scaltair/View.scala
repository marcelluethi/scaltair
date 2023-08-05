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
package scaltair

enum MarkType:
  case Line extends MarkType
  case Circle extends MarkType
  case Rect extends MarkType
  case Point extends MarkType
  case Bar extends MarkType
  case Area extends MarkType
  case Boxplot extends MarkType
  case Errorband extends MarkType
  case Text extends MarkType

private case class Mark(
    private[scaltair] val markType: MarkType,
    private[scaltair] val clip: Boolean,
    private[scaltair] val opacity: Double
):

  def opacity(opacity: Double): Mark = copy(opacity = opacity)
  def clip(clip: Boolean): Mark = copy(clip = clip)

object Mark:
  def Line() = Mark(MarkType.Line, false, 1.0)
  def Circle() = Mark(MarkType.Circle, false, 1.0)
  def Rect() = Mark(MarkType.Rect, false, 1.0)
  def Point() = Mark(MarkType.Point, false, 1.0)
  def Bar() = Mark(MarkType.Bar, false, 1.0)
  def Area() = Mark(MarkType.Area, false, 1.0)
  def Boxplot() = Mark(MarkType.Boxplot, false, 1.0)
  def ErrorBand() = Mark(MarkType.Errorband, false, 1.0)
  def Text() = Mark(MarkType.Text, false, 1.0)

trait View

private case class SingleView(
    mark: Mark,
    channels: Seq[Channel],
    clip: Boolean,
    opacity: Double
) extends View

private case class LayeredView(views: Seq[SingleView]) extends View
