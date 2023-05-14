# Scaltair

Simple plotting library for Scala based on [Vega-Lite](https://vega.github.io/vega-lite/), inspired by Python's [Vega-Altair](https://altair-viz.github.io/gallery/index.html).

Plots can be rendered in the browser or from within a jupyter-notebook. 

### Getting started

See the [quickstart Guide](docs/quickstart.md). 


### Status of the project

The project is in an early stage and the api might still be subject to changes. 
It supports a subset of the functionality provided by 
vega-lite and altair. While it can already produce most plots used in a typical scientific context or 
data analysis task, it cannot create interactive plots nor does it support temporal data. 

It should be easy to add the missing functionality if this is required. The project is designed in such a way, that it is easy to understand and straight-forward to extend. It does not make use of any fancy language features or libraries. Pull requests are very welcome.

##### Adding new features
Adding new features consists of two simple steps:

1. Add a case class in the package `scaltair.vegalite` representing the corresponding Vega construct and define how it is mapped to json. 
2. Expose the functionality in the high-level dsl defined in `scaltair.Chart`. 
