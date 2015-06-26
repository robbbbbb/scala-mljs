package com.robminson.ml

import com.robminson.ml.stats.KMeans
import com.robminson.ml.stats.KMeans.Centroid

import scala.annotation.meta.field
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.JSConverters._

@JSExport
case class Point(@(JSExport @field) x: Double, @(JSExport @field) y: Double)

@JSExport
case class Cluster(@(JSExport @field) centroid: Point, @(JSExport @field) points: Seq[Point])
object Cluster {
  def apply(centroid: Centroid, points: Iterable[Product]): Cluster = {
    val centroidAsPoint = Point(centroid.coords(0), centroid.coords(1))
    Cluster(centroidAsPoint, points.map(_.asInstanceOf[Point]).toSeq)
  }
}

@JSExport
object KMeansJSInterface {
  @JSExport
  def getClusters(pointsJs: scalajs.js.Array[Point], clusters: Int) = {
    val kMeansClusters = KMeans.calculate(pointsJs.toSeq, clusters)
    kMeansClusters.map { case (centroid, points) => Cluster(centroid, points) }.toSeq.toJSArray
  }
}
