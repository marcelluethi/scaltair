# Scaltair

To run the code in this tutorial, we need the following imports 
```scala mdoc:silent
import scaltair.plot.{Plot, Data}
import scaltair.plot.plottarget.PlotTargets.plotTargetBrowser
```

## Preparing the plot data

Before we can create a plot, we need to organize the data. 
Data is organized in a tabular format, where each column consists
of the same number of rows. Technically, it is implemented as a Map. 

As an example, we create data for plotting a function: $f : x \mapsto x^2$ where
$x \in [0, 100]$. We would prepare the data as follows:

```scala mdoc:silent
val xs = Seq.range(0, 100)
val ys = xs.map(x => x * x)
val data = Map("x" -> xs, "y" -> ys)
```

Sometimes it is easier to create the data row-wise. In this case we would write
```scala mdoc:silent
val data2 = Data.
```

```scala mdoc:silent
val xs = Seq(1.0, 2.0, 3.0, 4.0)
val df = DataFrame(
    Seq(
        DataFrame.Column.ofContinuous(xs, "x"),
        DataFrame.Column.ofContinuous(xs.map(x => x * x), "x squared"),
        DataFrame.Column.ofContinuous(xs.map(x => Math.sin(x)), "sin(x)"),
      )
    )
```
The three columns are named ```x```, ```x squared``` and ```sin(x)```and consist of a sequence of values of 
type ```Double```, which are interpreted as continuous value.  

Printing the dataframe displays the data aligned in columns
```scala mdoc
println(df)
```

Instead of specifying the data manually, we can read them from a csv file:
```scala mdoc:silent
val boneData = DataFrame.fromCSV(new java.io.File("data.csv")).get
```


## Plotting the data

Organizing the data in data frames give us enough structure that we can 
create effective data visualizations. Scalismo-plot supports a number of 
different plotting types.

### Line plots
The simplest plot we can create is 
a line plot
```scala mdoc:silent
  df.plot.linePlot(
    x = "x", 
    y = "sin(x)",  
    title=" A line plot"
  ).show()
``` 
We call the plot method on the dataframe and specify what kind of plot we want to create. 
We then specify the fields we use for indexing (here ```x```), and the field whose
value we want to plot (here ```sin(x)```). 

The result looks as follows:
![lineplot1](plots/lineplot1.svg)

Similarly, we could plot the second function using 
```scala mdoc:silent
df.plot.linePlot(
  x = "x", 
  y = "x squared",  
  title=" Another line plot"
).show()
```

![lineplot1](plots/lineplot2.svg)

#### Plotting series of functions

In this case we just specified a single function. We can also plot a series of functions. 
To be able to plot series, we will need to rearange our data. We need an 
additional column, which holds the label for the series. Each row is than interpreted
as being a data points for the series with the given label. 
Consider our example dataframe from above where we have two functions ```sin(x)``` and ```x squared```.
Our goal is to plot them as series. We create a new data frame for each series as follows:
```scala mdoc:silent
 val dfSeries1 = DataFrame(
      Seq(
        DataFrame.Column.ofNominals(Seq.fill(df.numberOfRows)("x squared"), "series"),
        df.column("x"),
        df.column("x squared").rename("y")
        )       
    )
    val dfSeries2 = DataFrame(Seq(
        DataFrame.Column.ofNominals(Seq.fill(df.numberOfRows)("sin(x)"), "series"),
        df.column("x"),
        df.column("sin(x)").rename("y")
        )       
      ) 
```
This new dataframes looks as follows:
```scala mdoc
println(dfSeries1)
println(dfSeries2)
```
 

Note that both data frames need to have the same number of columns and the names fo the 
columns need to be the same. We can then create a common data frame by concatenating the two. 
This new data frame can then be plotted. 
```scala mdoc:silent
    dfSeries1.concat(dfSeries2)
      .plot.linePlot(
        x = "x", 
        y = "y", 
        series = "series", 
        title = "Series plot")
      .show()
```

![lineplot1](plots/lineseries.svg)
    

### Plotting types

Scalismo-plot currently supports a number of different plotting types, as illustred
in the following examples. 

##### Scatterplot
```scala mdoc:silent
boneData.plot.scatterPlot(
  x = "stature", 
  y = "bone-length", 
  title = "scatterplot", 
  colorField = "sex").show()
```

![lineplot1](plots/scatterplot.svg)


##### Boxplots

```scala mdoc:silent
val r = scala.util.Random(42)
  DataFrame(Seq(
    DataFrame.Column.ofNominals(Seq.fill(30)("series-0"), "series"),
    DataFrame.Column.ofContinuous(Seq.fill(30)(r.nextGaussian), "values"))
  ).concat(
    DataFrame(Seq(
      DataFrame.Column.ofNominals(Seq.fill(30)("series-1"), "series"),
      DataFrame.Column.ofContinuous(Seq.fill(30)(r.nextGaussian + 1), "values")
    ))
  ).concat(
   DataFrame(Seq(
      DataFrame.Column.ofNominals(Seq.fill(30)("data-0"), "series"),
      DataFrame.Column.ofContinuous(Seq.fill(30)(r.nextGaussian + 1), "values")
    ))
  )
  .plot
  .boxplot(
    series = "series", 
    values = "values", 
    title ="Boxplot"
  ).show()
```

![lineplot1](plots/boxplot.svg)

##### Histogram

```scala mdoc:silent
boneData.plot.histogram(
  x = "stature", 
  title = "Stature").show()
```

![lineplot1](plots/histogram.svg)


##### Pair plots

```scala mdoc:silent
boneData.plot.pairPlot(
  columnNames = Seq("stature", "bone-length", "trochanter-distance"), 
  title = "Pair plot"
).show()
```

![lineplot1](plots/pairplot.svg)

##### Error Band

```scala mdoc:silent
val xValues = Seq(1.0, 2.0, 3.0, 4.0)
val dfWithError = DataFrame(
    Seq(
        DataFrame.Column.ofContinuous(xValues, "x"),
        DataFrame.Column.ofContinuous(xValues.map(x => x * x), "x squared"),
        DataFrame.Column.ofContinuous(xValues.map(x => x * x - 5), "lower"),
        DataFrame.Column.ofContinuous(xValues.map(x => x * x + 5), "upper"),
        
      )
    )
dfWithError.plot.linePlotWithErrorBand(
  x = "x", 
  y = "x squared",  
  lowerBand = "lower", 
  upperBand = "upper", 
  title=" A line plot with error bars"
).show()
```

![lineplot1](plots/errorband.svg)