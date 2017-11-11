lazy val commonSettings = Seq(
  version := "0.1.0",
  organization := "org.csw",
  scalaVersion := "2.12.4",
  scalacOptions ++= Seq(
    "-encoding",
    "UTF-8",
    "-feature",
    "-unchecked",
    "-deprecation",
    //"-Xfatal-warnings",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Xfuture"
  ),
  libraryDependencies ++= Seq(
    "org.scalatest" %%% "scalatest" % "3.0.3" % Test
  )
)

lazy val shared = crossProject
  .crossType(CrossType.Pure)
  .settings(commonSettings)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js

lazy val client = project
  .enablePlugins(ScalaJSPlugin, ScalaJSBundlerPlugin)
  .dependsOn(sharedJs)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.3",
      "io.github.outwatch" %%% "outwatch" % "0.10.2"
    ),
    npmDependencies in Compile ++= Seq(
      "snabbdom" -> "0.6.9",
      "font-awesome" -> "4.7.0",
      "url-loader" -> "0.5.9"
    ),
    jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv(),
    scalacOptions += "-P:scalajs:sjsDefinedByDefault"
  )

lazy val server = project
  .dependsOn(sharedJvm)
  .enablePlugins(WebScalaJSBundlerPlugin, SbtTwirl, JavaAppPackaging)
  .settings(commonSettings)
  .settings(
    scalaJSProjects := clientProjects,
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(gzip),
    // Expose as sbt-web assets some files retrieved from the NPM packages of the `client` project
    npmAssets ++= NpmAssets.ofProject(client) { modules => (modules / "font-awesome").allPaths }.value,
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http" % "10.0.10"
    ),
    WebKeys.packagePrefix in Assets := "public/",
    managedClasspath in Runtime += (packageBin in Assets).value
  )

// loads the server project at sbt startup
onLoad in Global := (onLoad in Global).value andThen { s: State => "project server" :: s }

def clientProjects = sys.props.get("CommandLine") match {
  case Some("true") => Seq(client)
  case _            => Seq.empty
}
