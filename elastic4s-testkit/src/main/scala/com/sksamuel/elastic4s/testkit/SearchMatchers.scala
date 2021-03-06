package com.sksamuel.elastic4s.testkit

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.{ElasticClient, SearchDefinition}
import org.scalatest.Matchers
import org.scalatest.matchers.{MatchResult, Matcher}

import scala.concurrent.duration._

trait SearchMatchers extends Matchers {

  def containResult(expectedId: Any)
                   (implicit client: ElasticClient,
                    timeout: FiniteDuration = 10.seconds): Matcher[SearchDefinition] = new Matcher[SearchDefinition] {
    override def apply(left: SearchDefinition): MatchResult = {
      val resp = client.execute(left).await(timeout)
      val exists = resp.hits.exists(_.id == expectedId.toString)
      MatchResult(
        exists,
        s"Search $left did not find document $expectedId",
        s"Search $left found document $expectedId"
      )
    }
  }

  def haveFieldValue(value: String)
                          (implicit client: ElasticClient,
                           timeout: FiniteDuration = 10.seconds): Matcher[SearchDefinition] = new
      Matcher[SearchDefinition] {
    override def apply(left: SearchDefinition) = {
      val resp = client.execute(left).await(timeout)
      val exists = resp.hits.exists(_.fields.exists(_._2.getValues.contains(value)))
      MatchResult(
        exists,
        s"Search $left contained unwanted field value $value",
        s"Search $left did not contain unwanted field value $value"
      )
    }
  }

  def haveSourceField(value: String)
                     (implicit client: ElasticClient,
                      timeout: FiniteDuration = 10.seconds): Matcher[SearchDefinition] = new Matcher[SearchDefinition] {
    override def apply(left: SearchDefinition) = {
      val resp = client.execute(left).await(timeout)
      val exists = resp.hits.exists(_.sourceAsMap.contains(value))
      MatchResult(
        exists,
        s"Search $left contained unwanted source field $value",
        s"Search $left did not contain unwanted source field $value"
      )
    }
  }

  def haveSourceFieldValue(value: String)
                          (implicit client: ElasticClient,
                           timeout: FiniteDuration = 10.seconds): Matcher[SearchDefinition] = new
      Matcher[SearchDefinition] {
    override def apply(left: SearchDefinition) = {
      val resp = client.execute(left).await(timeout)
      val exists = resp.hits.exists(_.sourceAsMap.valuesIterator.exists(_.toString == value))
      MatchResult(
        exists,
        s"Search $left contained unwanted source field value $value",
        s"Search $left did not contain unwanted source field value $value"
      )
    }
  }

  def haveTotalHits(expectedCount: Int)
                   (implicit client: ElasticClient,
                    timeout: FiniteDuration = 10.seconds): Matcher[SearchDefinition] = new Matcher[SearchDefinition] {
    override def apply(left: SearchDefinition) = {
      val resp = client.execute(left).await(timeout)
      val count = resp.totalHits
      MatchResult(
        count == expectedCount,
        s"Search $left found $count totalHits",
        s"Search $left found $count totalHits"
      )
    }
  }

  def haveHits(expectedCount: Int)
              (implicit client: ElasticClient,
               timeout: FiniteDuration = 10.seconds): Matcher[SearchDefinition] = new Matcher[SearchDefinition] {
    override def apply(left: SearchDefinition) = {
      val resp = client.execute(left).await(timeout)
      val count = resp.hits.length
      MatchResult(
        count == expectedCount,
        s"Search $left found $count hits",
        s"Search $left found $count hits"
      )
    }
  }

  def haveNoHits(implicit client: ElasticClient,
                 timeout: FiniteDuration = 10.seconds): Matcher[SearchDefinition] = new Matcher[SearchDefinition] {
    override def apply(left: SearchDefinition) = {
      val resp = client.execute(left).await(timeout)
      val count = resp.hits.length
      MatchResult(
        count == 0,
        s"Search $left found $count hits",
        s"Search $left found $count hits"
      )
    }
  }
}
