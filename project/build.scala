import sbt._
import sbt.Keys._

object MLBuild extends Build {
  lazy val root = Project(
    id = "scalaml",
    base = file("."),
    settings =
      Project.defaultSettings ++
      Seq()
  )
}