import sbt.Keys.libraryDependencies

name := "predict-stock-in-spark"

version := "1.0"

scalaVersion := "2.11.8"

val spark_scope = System.getProperty("spark.scope", "compile")
val spark_version = System.getProperty("spark.version", "2.2.1")

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")
initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
  // sprak 版本如果是2.2.1, 要求jvm基于1.8编译, 这里写死要求1.8
    sys.error("Java 8 is required for this project.")
}

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % spark_version % spark_scope,
  "org.apache.spark" %% "spark-streaming" % spark_version % spark_scope,
  "org.apache.spark" %% "spark-mllib" % spark_version % spark_scope
)

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies  ++= Seq(
  // Last stable release
  "org.scalanlp" %% "breeze" % "0.13.2",

  // Native libraries are not included by default. add this if you want them (as of 0.7)
  // Native libraries greatly improve performance, but increase jar sizes.
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  "org.scalanlp" %% "breeze-natives" % "0.13.2",

  // The visualization library is distributed separately as well.
  // It depends on LGPL code
  "org.scalanlp" %% "breeze-viz" % "0.13.2"
)