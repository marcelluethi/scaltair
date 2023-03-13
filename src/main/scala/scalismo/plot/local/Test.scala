package scalismo.plot.local

import scalismo.plot.data.DataFrame
import scalismo.plot.plottarget.PlotTargets.plotTargetBrowser
import scalismo.plot.data.DataFrame.CellValue

object Test:
    def main(args : Array[String]) : Unit = 
        // the values on the x-axis
        val xs = (0 to 10).map(_.toDouble)

        // we create for each line that we would like to plot a separate data frame, 
        // consisting of a column x and y with the x and y values, and a name for the series
        val dataFrames = for i <- 0 until 10 yield 
            
            val fun = (x : Double) => x + i // the function
            val df = DataFrame.fromColumns(
                Seq(
                    DataFrame.Column.ofContinuous(xs, "x"),
                    DataFrame.Column.ofContinuous(xs.map(x => fun(x)), "y"),
                    DataFrame.Column.ofNominals(Seq.fill(xs.length)(s"function $i"), name = "series")
                )
            )
            print(df)
            df

        val df = dataFrames.head
        val x = df.rows(0)
        x(0).value match
            case CellValue.Continuous(v) => println(v)
            case _ => println("not a continuous value")
        


        // we concatenate all the dataframes into one
        val combinedDataFrameForSeries = dataFrames.reduce((acc, x) => acc.concat(x))

        // we use the new data frame to plot the series
        combinedDataFrameForSeries.plot.linePlot(x = "x", y = "y", series = "series", title="a series of linear functions").show() 
