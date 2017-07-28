package com.experiments.calvin

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import com.experiments.calvin.data.Tweet
import org.scalatest.{FunSpecLike, MustMatchers}

class FlowSpec extends TestKit(ActorSystem("akka-streams-test")) with FunSpecLike with MustMatchers {
  describe("Merge Preferred Source Specification") {
    import flows._
    it("pulls messages from the prioritized source first followed by a combination of interleaving all other sources") {
      implicit val mat = ActorMaterializer()(system)
      val lowPriorityTweetA = Tweet(id = 1, text = "low-priority-A")
      val lowPriorityTweetB = Tweet(id = 2, text = "low-priority-B")
      val highPriorityTweet = Tweet(id = 3, text = "high-priority")

      val lowPriorityElementsA = List.fill(10)(lowPriorityTweetA)
      val lowPriorityElementsB = List.fill(10)(lowPriorityTweetB)
      val highPriorityElements = List.fill(10)(highPriorityTweet)

      val interleavedPrioritizedSource = mergePreferredSource(
        Source(highPriorityElements), Source(lowPriorityElementsA), Source(lowPriorityElementsB)
      )

      // http://doc.akka.io/docs/akka/current/scala/stream/stream-testkit.html
      val sinkSubscriber = interleavedPrioritizedSource.runWith(TestSink.probe[Tweet])

      // Expect high priority tweets to go first (when asking for elements one at a time)
      // Note: the behavior changes when asking for more than a single element and you may get lower priority
      // elements appearing within the first 10 elements (try request(30) to observe this)
      highPriorityElements.foreach(tweet =>
        sinkSubscriber.request(1).expectNext(tweet)
      )

      // interleaving starts happening here, we are only concerned that we get 20 elements and nothing more
      val lowPriorityResultsFromStream = sinkSubscriber.request(20).expectNextN(20)

      // 10 of low-priority-A and 10 of low-priority-B
      lowPriorityResultsFromStream.count(_.id == lowPriorityTweetA.id) mustBe lowPriorityElementsA.length
      lowPriorityResultsFromStream.count(_.id == lowPriorityTweetB.id) mustBe lowPriorityElementsB.length
      lowPriorityResultsFromStream.length mustBe (lowPriorityElementsA ++ lowPriorityElementsB).length

      sinkSubscriber.request(1).expectComplete()
    }
  }
}
