organization := "ch.unibas.cs.gravis"

name := """scalismo-plot"""
version := "0.1-SNAPSHOT"

fork := true

scalaVersion := "3.1.0"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers += Opts.resolver.sonatypeSnapshots
resolvers += Opts.resolver.sonatypeReleases

lazy val root = (project in file("."))
  .settings(
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    mdocIn := new java.io.File("docs/mdoc"),
    mdocOut := new java.io.File("docs/")
  )
  .enablePlugins(MdocPlugin)
