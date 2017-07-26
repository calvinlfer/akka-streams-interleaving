package com.experiments.calvin

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object data {
  // Data
  case class Tweet(id: Int, text: String)

  // JSON support
  object TweetJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
    implicit val tweetFormat: RootJsonFormat[Tweet] = jsonFormat2(Tweet)
  }
}
