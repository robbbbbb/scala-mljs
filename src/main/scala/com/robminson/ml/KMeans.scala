package com.robminson.ml.stats

import scala.annotation.tailrec

// A crummy implementation of k-means that uses some hackery to allow you to run it over population of instances of
// arbitrary case class (assuming all the case classes involved contain the same number of fields and the fields are
// all of numeric types).
// You can call it like this:
//
//    KMeans.calculate(
//      Seq(Foo(3.3, 5.2), Foo(1.5, 8.8), Bar(2.3, 0.1), ...),
//      5
//    )
//
// and it will converge a clustering of your Foo instances for 5 clusters (see main method at the bottom for example).
//
// Caveats:
//  - How you calculate k is up to you, it doesn't work out the optimal number of clusters for you.
//  - The space is auto-normalised and assumed to be cartesian, if that's not the case, you're boned.
//  - If your Foo class contains a non-numeric field it'll crash at runtime, map it down in to something that conforms

object KMeans {


//--- CORE ALGORITHM

  def calculate(population: Iterable[Product], k: Int): Clustering = {
    converge(population, None, k)
  }

  // a single iteration of the algorithm, left public to help with visualisation
  def iterate(  population: Iterable[Product],
                currentState: Option[Clustering],
                k: Int): Clustering = {
    val popBounds = bounds(population)
    val newCentroids = currentState match {
      case Some(state) =>
        println(s"Converging...${state.map { case (_, cluster) => s"${cluster.size}" } mkString(",")}")
        state.values.map(calculateCentroid(_)).toList
      case _ => Range(0, k).map(_ => randomCentroid(popBounds)).toList
    }
    assignPopulation(population, newCentroids, popBounds)
  }

  // Core of the K-Means algorithm
  // a recursive function that recalculates the centroids given the current clustering, then determines if the
  // clusters have changed:
  //  if so we need to recalculate again
  //  if not we have converged so the state is the final one
  @tailrec
  private def converge( population: Iterable[Product],
                        currentState: Option[Clustering],
                        k: Int): Clustering = {
    val newState = iterate(population, currentState, k)
    if (currentState.isDefined && clusteringEqual(currentState.get, newState))
      newState
    else
      converge(population, Some(newState), k)
  }


//--- UTILS ---

  // Some extenstion methods to allow us to work in a sane way with instances of Product (which all case classes extend)
  implicit class ProductExtensions(val p: Product) {
    def doubleElement(idx: Int) = p.productElement(idx).asInstanceOf[Double]
    def approxEqual(other: Product): Boolean = {
      Range(0, p.productArity).forall(idx => math.abs(p.doubleElement(idx) - other.doubleElement(idx)) < Double.MinPositiveValue)
    }
  }

  // An arbitrary dimension coordinate for modelling the centroid of a cluster
  case class Centroid(coords: List[Double]) {
    override def toString = s"Centroid(${coords.map(c => f"$c%.2f").mkString(", ")})"
  }

  type Clustering = Map[Centroid, Iterable[Product]]
  private def clusteringEqual(a: Clustering, b: Clustering) = {
    // a clustering is equal if all the populations of the clusters are the same, we don't care about the exact centroids
    a.forall {
      case (_, cluster) => b.values.exists(otherCluster => cluster.forall(individual => otherCluster.toList.contains(individual)))
    }
  }

  // Calculates the min,max bounds of a given population for each dimension.
  // More exactly it maps a Seq[Foo] to a Seq[(min, max)] where the latter sequence has one (min, max) pair for
  // each field in class Foo
  private def bounds(population: Iterable[Product]): List[(Double, Double)] = {
    if (population.size > 0) {
      Range(0, population.head.productArity) map { idx =>
        (
          population.map(_.doubleElement(idx)).min,
          population.map(_.doubleElement(idx)).max
        )
      } toList
    }
    else
    List.empty
  }

  // calculate the centroid of the given population
  private def calculateCentroid(population: Iterable[Product]): Centroid = {
    Centroid(
      Range(0, population.head.productArity) map { idx =>
        population.map(_.doubleElement(idx)).sum / population.size.toDouble
      } toList
    )
  }

  // gives the cartesian distance between the N fields of individual and the first N elements of centroid
  // using the bounds argument to normalise in each dimension
  private def distance(individual: Product, centroid: Centroid, bounds: List[(Double, Double)]): Double = {
    val distanceVector = Range(0, individual.productArity) map { idx =>
      math.abs(centroid.coords(idx) - individual.doubleElement(idx)) / (bounds(idx)._2 - bounds(idx)._1)
    }
    math.sqrt(distanceVector.map(e => math.pow(e, 2)).sum)
  }

  // gives a random centroid lying within the given bounds
  private def randomCentroid(bounds: List[(Double, Double)]): Centroid = {
    Centroid(bounds.map { case (min, max) => min + (max - min) * math.random })
  }

  // get the closest centroid to the given individual
  private def closestCentroid(individual: Product, centroids: List[Centroid], bounds: List[(Double, Double)]): Centroid = {
    centroids.minBy(c => distance(individual, c, bounds))
  }

  // assign population to the given set of centroids
  private def assignPopulation(population: Iterable[Product], centroids: List[Centroid], bounds: List[(Double, Double)]): Map[Centroid, Iterable[Product]] = {
    population.groupBy(closestCentroid(_, centroids, bounds))
  }

  def main(args: Array[String]) = {
    case class Foo(x: Double, y: Double, z: Double) { override def toString = f"Foo($x%.2f, $y%.2f, $z%.2f)" }

    val popSize = 100
    val bimodalPopulation: List[Foo] = Range(0, popSize).map { i =>
      val modalCenter = if (i < popSize / 2) 5 else 95
      def ran = modalCenter + (-5 + math.random * 10)
      Foo(ran, ran, ran)
    }.toList
//    val population: List[Foo] = Range(0, popSize).map(_ => Foo(math.random * 10, math.random * 10, math.random * 10)).toList
    val clustering = calculate(bimodalPopulation, 4)
    clustering foreach { case (centroid, cluster) =>
      println(s"\n$centroid\n\t${cluster.mkString("\n\t")}")
    }
  }
}
