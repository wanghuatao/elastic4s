package com.sksamuel.elastic4s

import com.sksamuel.elastic4s.source.{DocumentSource, Indexable}
import play.api.libs.json.{Json, Reads, Writes}

import scala.annotation.implicitNotFound

package object playjson {
  @implicitNotFound("No Writes for type ${T} found. Bring an implicit Writes[T] instance in scope")
  implicit def playJsonIndexable[T](implicit w: Writes[T]) = new Indexable[T] {
    override def json(t: T): String = Json.stringify(Json.toJson(t)(w))
  }

  @implicitNotFound("No Reads for type ${T} found. Bring an implicit Reads[T] instance in scope")
  implicit def playJsonHitAs[T](implicit r: Reads[T]) = new HitAs[T] {
    override def as(hit: RichSearchHit): T = Json.parse(hit.sourceAsString).as[T]
  }

  @implicitNotFound("No Writes for type ${T} found. Bring an implicit Writes[T] instance in scope")
  implicit def playJsonDocumentSource[T](t: T)(implicit w: Writes[T]) = new DocumentSource {
    override def json: String = Json.stringify(Json.toJson(t)(w))
  }
}
