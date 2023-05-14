lazy val root = (project in file("."))
  .settings(
    organization := "ch.unibas.cs.gravis",
    name := """scaltair""",
    version := "0.1-SNAPSHOT",
    scalaVersion := "3.2.0",
    
    homepage := Some(url("https://github.com/marcelluethi/scaltair")),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    scmInfo := Some(
      ScmInfo(url("https://github.com/marcelluethi/scaltair"), "git@github.com:marcelluethi/scaltair.git")
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
    mdocOut := new java.io.File("docs/"), 
    libraryDependencies += ("sh.almond" % "scala-kernel-api" % "0.13.0" % Provided)
        .cross(CrossVersion.for3Use2_13With("", ".7"))
        .exclude("com.lihaoyi", "geny_2.13")
        .exclude("com.lihaoyi", "sourcecode_2.13")
        .exclude("com.lihaoyi", "fansi_2.13")
        .exclude("com.lihaoyi", "os-lib_2.13")
        .exclude("com.lihaoyi", "pprint_2.13")
        .exclude("org.scala-lang.modules", "scala-collection-compat_2.13")
        .exclude("com.github.jupyter", "jvm-repr"),




  )
  .enablePlugins(MdocPlugin)
