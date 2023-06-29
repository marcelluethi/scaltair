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

import java.awt.Desktop
import java.nio.file.Paths
import java.nio.file.Files
import java.net.URI
import scaltair.vegalite.VegaChart
import scaltair.json.JsonObject
import scaltair.json.Json
import scala.util.Success
import scala.util.Try

trait PlotTarget:
  def show(chart: VegaChart): Unit

object PlotTargetBrowser extends PlotTarget:

  given plotTargetBrowser: PlotTarget = PlotTargetBrowser

  private final val FILE_PREFIX = "scalismo-plot-"
  private final val FILE_SUFFIX = ".html"

  def show(chart: VegaChart): Unit =
    val spec = chart.spec

    // We write the spec to the file system and open it in the browser.
    // To avoid cluttering the file system, we clean up all files in the
    // temporary directory we might have written in previous runs.
    cleanupOldFiles()
    val tmpURI = writeSpecToFile(spec)

    if (
      Desktop.isDesktopSupported() && Desktop
        .getDesktop()
        .isSupported(Desktop.Action.BROWSE)
    )
    then Desktop.getDesktop().browse(tmpURI)
    else
      // fallback to xdg-open on linux
      try (Runtime.getRuntime().exec("xdg-open " + tmpURI)) 
      catch case (e : Throwable) => throw Exception("Could not open browser. Please open the following file manually: " + tmpURI)

  private def writeSpecToFile(spec: JsonObject): URI =

    val renderedSpec = Json.stringify(spec)
    val theHtml = raw"""<!DOCTYPE html>
                <html>
                <head>
                <meta charset="utf-8" />
                <!-- Import Vega & Vega-Lite -->
                <script src="https://cdn.jsdelivr.net/npm/vega@5"></script>
                <script src="https://cdn.jsdelivr.net/npm/vega-lite@5"></script>
                <!-- Import vega-embed -->
                <script src="https://cdn.jsdelivr.net/npm/vega-embed@5"></script>
                <style>
                    div#vis {
                        width: 95vmin;
                        height:95vmin;
                        style="position: fixed; left: 0; right: 0; top: 0; bottom: 0"
                    }
                </style>
                </head>
                <body>            
                    <div id="vis"></div>

                <script type="text/javascript">
                const spec = ${renderedSpec};  
                vegaEmbed('#vis', spec, {
                    renderer: "canvas", // renderer (canvas or svg)
                    container: "#vis", // parent DOM container
                    hover: true, // enable hover processing
                    actions: {
                    editor : true
                    }
                }).then(function(result) {

                })
                </script>
                </body>
                </html> """
    val tempFi = writeToTempFile(theHtml)
    tempFi.toUri()

  private def writeToTempFile(content: String) =
    val tempFile = Files.createTempFile(FILE_PREFIX, FILE_SUFFIX)
    tempFile.toFile
    Files.write(tempFile, content.getBytes)

    tempFile

  private def cleanupOldFiles(): Unit =
    val tempDir = Paths.get(System.getProperty("java.io.tmpdir"))
    val files = tempDir.toFile().listFiles()
    
    // delete all files that start with the given prefix and end on the suffix
    // and which are older than 1 minute
    files.foreach( f =>
      if (f.getName().startsWith(FILE_PREFIX) && f.getName().endsWith(FILE_SUFFIX)) then
        val lastModified = f.lastModified()
        val now = System.currentTimeMillis()
        val age = now - lastModified
        if (age > 60000) then
          f.delete()        
    )
    
