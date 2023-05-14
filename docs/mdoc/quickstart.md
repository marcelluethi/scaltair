# Scaltair

To run the code in this tutorial, we need the following imports 

```scala mdoc:silent
import scaltair.{Chart, Data}
import scaltair.FieldType
import scaltair.Channel
import scaltair.plottarget.PlotTargets.plotTargetBrowser
import scaltair.ChartProperties
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

Sometimes it is easier to create the data one row at the time. 
This can be achieved by using the method `Data.fromRows` as follows:
```scala mdoc:silent
val dataRowWise = Data.fromRows(
    xs.zip(ys).map(
        (x, y) => Map("x" -> x, "y" -> y)
    )
)
```

## A first plot

Now that the data is in place, we can create the first plot. 
This is achieved using the following code, which also 
aims to illustrate all the main elements involved for specifying
a plot in Scaltair:

```scala mdoc:silent
Chart(data)
    .encode(
        Channel.X("x", FieldType.Quantitative), 
        Channel.Y("y", FieldType.Quantitative)
    )
    .markLine()
    .properties(
        ChartProperties(
            title="linechart",
            width = 500, 
            height = 500)
        )
    .show()

```

Any specification of the plot consists of the same 4 elements

1. Creation of the Chart object by providing it the data to be plotted.
2. Specification of which field of the data is mapped to which Channel in Vega and how the data should be 
interpreted.
3. Specification of how the data is visualized
4. Specification of additional (global) properties for the chart, such as width and title

The `show` function visualizes the created chart using the plottarget that was imported above. 
Currently, possible Plottargets include Browser and Jupyter-Notebook. 

## Addition examples

A wide variety of different plots can be created by following this simple scheme, but vary 
the mark or how data is encoded. Examples for creating basic plots like barcharts, scatterplots 
boxplots or bubbleplots can be found in the [Examples](../src/main/scala/scaltair/examples/SimpleCharts.scala). 

## Composite plots

Much of the power of Vega (and hence Scaltair) comes from the ability to compose simple charts
to produce more complicated once. This can be can be done by 1) overlaying differnet charts and 2)
aligning charts next to each other. 

Each chart definition above produces something that is called a *View*. Instead of calling `show`, 
we can first compose these views with the operators `overlay`, `hconcat` and `vconcat` as illustrated
in the following example:

```scala mdoc:silent
 val data2 = Map(
    "shoe-size" -> Seq(38, 42, 43, 44, 47),
    "stature" -> Seq(150, 170, 172, 180, 195),
    "weight" -> Seq(55, 75, 70, 75, 100),
    "sex" -> Seq("f", "f", "m", "m", "m")
  )

    val view1 = Chart(data2)
      .encode(
        Channel.X("shoe-size", FieldType.Ordinal),
        Channel.Y("stature", FieldType.Quantitative)
      )
      .markLine()

    val view2 = Chart(data2)
      .encode(
        Channel.X("shoe-size", FieldType.Ordinal),
        Channel.Y("stature", FieldType.Quantitative),
        Channel.Color("sex")
      )
      .markBar()

    view1.overlay(view2).show()
    view1.hConcat(view2).show()
```

### More examples and documentation

As Scaltairs approach to plotting follows closely the approach taken by [Vega-Altair](https://altair-viz.github.io/gallery/index.html), you may want to checkout [Altair's example gallery]([Vega-Altair](https://altair-viz.github.io/gallery/index.html))
