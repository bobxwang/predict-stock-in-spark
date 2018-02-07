import sbt.Keys.libraryDependencies

name:="predict-stock-in-spark"

version:="1.0"

scalaVersion:="2.11.8"

val spark_scope = System.getProperty("spark.scope", "compile")
val spark_version = System.getProperty("spark.version", "2.2.1")

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % spark_version % spark_scope,
  "org.apache.spark" %% "spark-streaming" % spark_version % spark_scope,
  "org.apache.spark" %% "spark-mllib" % spark_version % spark_scope
)