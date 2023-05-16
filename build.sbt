ThisBuild / organization := "ch.unibas.cs.gravis"
ThisBuild / version := "0.1-SNAPSHOT"
ThisBuild / scalaVersion := "3.2.0"

ThisBuild / homepage := Some(url("https://github.com/marcelluethi/scaltair"))
ThisBuild / licenses += ("Apache-2.0", url(
  "http://www.apache.org/licenses/LICENSE-2.0"
))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/marcelluethi/scaltair"),
    "git@github.com:marcelluethi/scaltair.git"
  )
)
ThisBuild / developers := List(
  Developer(
    "marcelluethi",
    "marcelluethi",
    "marcel.luethi@unibas.ch",
    url("https://github.com/marcelluethi")
  )
)
ThisBuild / versionScheme := Some("early-semver")

lazy val root = (project in file("."))
  .settings(
    name := """scaltair""",
    publishMavenStyle := true,
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),
    resolvers ++= Seq(
      Resolver.jcenterRepo,
      Resolver.sonatypeRepo("releases"),
      Resolver.sonatypeRepo("snapshots")
    ),
    scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case _ =>
        Seq(
          "-encoding",
          "UTF-8",
          "-Xlint",
          "-deprecation",
          "-unchecked",
          "-feature",
          "-target:jvm-1.8"
        )
    }),
    javacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case _ => Seq("-source", "1.8", "-target", "1.8")
    }),
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    mdocIn := new java.io.File("docs/mdoc"),
    mdocOut := new java.io.File("docs/")
  )
  .enablePlugins(MdocPlugin)

lazy val jupyter = (project in file("jupyter"))
  .settings(
    name := """scaltair-jupyter""",
    publishMavenStyle := true,
    publishTo := Some(
      if (isSnapshot.value)
        Opts.resolver.sonatypeSnapshots
      else
        Opts.resolver.sonatypeStaging
    ),
    libraryDependencies += (
      "sh.almond" % "scala-kernel-api" % "0.13.0" % Provided
    )
      .cross(CrossVersion.for3Use2_13With("", ".7"))
      .exclude("com.lihaoyi", "geny_2.13")
      .exclude("com.lihaoyi", "sourcecode_2.13")
      .exclude("com.lihaoyi", "fansi_2.13")
      .exclude("com.lihaoyi", "os-lib_2.13")
      .exclude("com.lihaoyi", "pprint_2.13")
      .exclude("org.scala-lang.modules", "scala-collection-compat_2.13")
      .exclude("com.github.jupyter", "jvm-repr")
  )
  .dependsOn(root)
