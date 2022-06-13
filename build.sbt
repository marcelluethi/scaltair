lazy val root = (project in file("."))
  .settings(
    organization := "ch.unibas.cs.gravis",
    name := """scalismo-plot""",
    version := "0.1-SNAPSHOT",
    scalaVersion := "3.1.0",
    
    homepage := Some(url("https://github.com/marcelluethi/scalismo-plot")),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scmInfo := Some(
      ScmInfo(url("https://github.com/marcelluethi/scalismo-plot"), "git@github.com:marcelluethi/scalismo-plot.git")
    ),
    developers := List(
      Developer("marcelluethi", "marcelluethi", "marcel.luethi@unibas.ch", url("https://github.com/marcelluethi"))
    ),
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
      Resolver.sonatypeRepo("snapshots"),      
    ),
    scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case _ => Seq("-encoding", "UTF-8", "-Xlint", "-deprecation", "-unchecked", "-feature", "-target:jvm-1.8")
    }),
    javacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
      case _ => Seq("-source", "1.8", "-target", "1.8")
    }),
    libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test,
    mdocIn := new java.io.File("docs/mdoc"),
    mdocOut := new java.io.File("docs/")
  )

  .enablePlugins(MdocPlugin)
