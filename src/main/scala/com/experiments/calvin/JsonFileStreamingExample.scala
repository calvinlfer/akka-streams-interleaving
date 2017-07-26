package com.experiments.calvin

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Sink}
import akka.stream.{ActorMaterializer, ThrottleMode}
import com.experiments.calvin.flows._

import scala.concurrent.duration._

/**
  * I want to read JSON Tweet data from a file and display it on screen so a human can follow along
  */
object JsonFileStreamingExample extends App {
  // Akka streams setup
  implicit val system = ActorSystem("akka-streams-file-json-streaming-example")
  implicit val mat = ActorMaterializer()

  val filePath = Paths.get("example.streaming.json")
  val fileSource = FileIO.fromPath(filePath)
  val result = fileSource
    .via(byteString2TweetFlow(1024))
    .throttle(elements = 1, per = 1.second, maximumBurst = 2, mode = ThrottleMode.Shaping)
    .to(Sink.foreach(println))
    .run()
}
