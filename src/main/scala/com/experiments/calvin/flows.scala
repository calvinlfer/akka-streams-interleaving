package com.experiments.calvin

import akka.NotUsed
import akka.stream.SourceShape
import akka.stream.scaladsl.{Flow, Framing, GraphDSL, MergePreferred, Source}
import akka.util.ByteString
import com.experiments.calvin.data.Tweet
import spray.json._


object flows {
  /**
    * A flow that allows you to connect a file based upstream stage and easily convert it to a Tweet
    * @param maxLine is the maximum amount of characters we are willing to parse from the upstream ByteString
    * @return a stream of Tweets
    */
  def byteString2TweetFlow(maxLine: Int): Flow[ByteString, Tweet, NotUsed] = {
    import data.TweetJsonProtocol._
    Framing.delimiter(ByteString("\n"), maxLine)
      .map(_.decodeString("UTF8"))
      .map(_.parseJson)
      .map(_.convertTo[Tweet])
  }

  /**
    * Source combinator that combines multiple Sources that produce the same type of data using Merge Preferred
    * semantics
    *
    * @param preferredSource is the preferred source that you want to give priority to over the other sources
    * @param rest are the rest of the sources that have lower priority than the preferred source
    * @tparam A is the type of element being emitted by all Sources
    * @return a unified source emitting A elements with the preferred source given priority over all others
    */
  def mergePreferredSource[A](preferredSource: Source[A, _], rest: Source[A, _]*): Source[A, NotUsed] =
    Source.fromGraph(GraphDSL.create() { implicit builder =>
      import GraphDSL.Implicits._
      // the number of secondary ports to allocate for MergePreferred is dependent upon the number of secondary sources
      val preferredMerge = builder.add(MergePreferred[A](secondaryPorts = rest.length, eagerComplete = false))

      /**
        * Source 1 (preferred) ---->  |-----------------|
        *                             | mergePreferred  |
        * Source 2 (secondary) ---->  |                 |
        *  .                          |                 |---> merged Source
        *  .                   ---->  |                 |
        *  .                          |                 |
        * Source N (secondary) ---->  |-----------------|
        */
      preferredSource ~> preferredMerge.preferred

      rest.foldLeft(0) { (nextPort, nextSource) =>
        nextSource ~> preferredMerge.in(nextPort)
        nextPort + 1
      }

      SourceShape(preferredMerge.out)
    })
}
