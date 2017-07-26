name := "akka-streams-json-merging-example"

version := "1.0"

scalaVersion := "2.12.2"

libraryDependencies ++= {
  val akka = "com.typesafe.akka"
  val akkaV = "2.5.3"
  Seq(
    akka %%     "akka-stream"           % akkaV,
    akka %%     "akka-stream-testkit"   % akkaV % Test,
    akka %%     "akka-http-spray-json"  % "10.0.9"
  )
}
