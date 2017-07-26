package com.experiments.calvin

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString
import com.experiments.calvin.flows._

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * I want to demonstrate giving priority to a single source of data amidst multiple sources of data and display this
  * on screen for a human to read
  */
object MergePreferredStreamInterleavingExample extends App {
  implicit val system = ActorSystem("akka-streams-file-json-streaming-example")
  implicit val mat = ActorMaterializer()
  implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global

  // Note: using static files + MergePreferred will cause the high priority file to be processed first
  // followed by the lower priority file. It's much better to work with continuous streams of data to see the
  // preferred interleaving of data
  val jsonSourceLowP = Source.tick(0.seconds, 500.milliseconds, ByteString("""{"id": 1, "text": "low-p"}""" + "\n"))
  val jsonSourceHighP = Source.tick(0.seconds, 1000.milliseconds, ByteString("""{"id": 2, "text": "hi-p"}""" + "\n"))
  val result =
    mergePreferredSource(jsonSourceHighP, jsonSourceLowP, jsonSourceLowP, jsonSourceLowP, jsonSourceLowP)
      .via(byteString2TweetFlow(1024))
      .to(Sink.foreach(println))
      .run()
}
