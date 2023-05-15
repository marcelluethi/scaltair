package scaltair.local

import scaltair.*
import scaltair.PlotTargets.plotTargetBrowser
import scaltair.vegalite.datasets.VegaDatasets


@main def examplePlot() = 
  val data = VegaDatasets.loadCars().get
  val plot = Chart(data)
    .encode(
      Channel.X("Horsepower", FieldType.Quantitative),
      Channel.Y("Miles_per_Gallon", FieldType.Quantitative),
      Channel.Color("Origin")
    )
    .markCircle()
    .show()

