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
  def initialise(points: scalajs.js.Array[Point], k: Int) = {
    clusteringToJS(KMeans.iterate(
      points.toSeq,
      None,
      k))
  }

  @JSExport
  def iterate(points: scalajs.js.Array[Point], clusters: scalajs.js.Array[Cluster], k: Int) = {
    clusteringToJS(KMeans.iterate(
      points.toSeq,
      if (clusters.isEmpty) None else Some(jsToClustering(clusters)),
      k))
  }

  private def clusteringToJS(clustering: KMeans.Clustering) = {
    clustering.
      map { case (centroid, points) => Cluster(centroid, points) }.
      toSeq.
      sortBy(_.centroid.x). // this just gives is a stable ordering to make visualisation easier
      toJSArray
  }

  private def jsToClustering(clustering: scalajs.js.Array[Cluster]): KMeans.Clustering = {
    clustering.
      groupBy(_.centroid).
      map { case(centroid, clusters) => (Centroid(List(centroid.x, centroid.y)), clusters.head.points) }
  }
}
