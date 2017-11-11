// fast development turnaround when using sbt ~reStart
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.2")

addSbtPlugin("com.typesafe.sbt" % "sbt-twirl" % "1.3.13")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.21")

addSbtPlugin("ch.epfl.scala" % "sbt-web-scalajs-bundler" % "0.9.0")

addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")

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
)
