# Scaltair

Simple plotting library for Scala based on [Vega-Lite](https://vega.github.io/vega-lite/), inspired by Python's [Vega-Altair](https://altair-viz.github.io/gallery/index.html).

Plots can be rendered in the browser or from within a jupyter-notebook. 

### Getting started

See the [quickstart Guide](docs/quickstart.md). 

You can also have a look at the 

### Status of the project

The project is in an early stage and the interface might still change. 
It supports a subset of the functionality provided by 
vega-lite and altair. In particular, it currently cannot create
interactive plots or working with temporal data. 

It should be easy to add the missing functionality if this is required. The project is designed in such a way, that it is easy to understand and does not make use of any fancy language 
features or libraries. Pull requests are very welcome.

##### Adding new features
Adding new features consists of two simple steps:

1. Add a Case class in the package scaltair.plot.vegalite representing the corresponding Vega construct.
2. Expose the functionality in the corresponding dsl. 
