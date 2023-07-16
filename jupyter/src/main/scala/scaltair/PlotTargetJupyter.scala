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

import almond.api.JupyterApi
import almond.interpreter.api.DisplayData
import almond.api.JupyterAPIHolder.value
import scaltair.json.Json
import scaltair.vegalite.VegaLiteDSL
import scaltair.vegalite.toJson   

object PlotTargetJupyter extends PlotTarget:

  given plotTargetJupyter: PlotTarget = PlotTargetJupyter


  def show(spec: VegaLiteDSL): Unit =

    // This code was taken from  https://github.com/Quafadas/dedav4s/tree/main/core/jvm/src/main/scala/viz
    val kernel = summon[JupyterApi]
    kernel.publish.display(
      DisplayData(
        data = Map(
          "application/vnd.vega.v5+json" -> Json.stringify(spec.toJson())
        )
      )
    )
