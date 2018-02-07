package com.bob.sql

import com.bob.BaseApp
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

case class Person(name: String, age: Long)

object SSql extends BaseApp {

  val path = "src/main/resources/people.json"
  val df: DataFrame = spark.read.json(path)
  df.show()

  import spark.implicits._

  df.printSchema()
  df.select("name").show()
  df.select($"name", $"age" + 1).show()
  df.filter($"age" > 21).show()
  df.groupBy("age").count().show()

  // emporary views in Spark SQL are session-scoped and will disappear if the session that creates it terminates.
  df.createOrReplaceTempView("people")
  val sqlDF = spark.sql("select * from people")
  sqlDF.show()

  // Register the DataFrame as a global temporary view
  df.createGlobalTempView("people")
  // Global temporary view is tied to a system preserved database `global_temp`
  spark.sql("SELECT * FROM global_temp.people").show()
  // Global temporary view is cross-session
  spark.newSession().sql("SELECT * FROM global_temp.people").show()

  val caseClassDS = Seq(Person("Andy", 32)).toDS()
  caseClassDS.show()
  // +----+---+
  // |name|age|
  // +----+---+
  // |Andy| 32|
  // +----+---+

  // Encoders for most common types are automatically provided by importing spark.implicits._
  val primitiveDS = Seq(1, 2, 3).toDS()
  primitiveDS.map(_ + 1).collect() // Returns: Array(2, 3, 4)

  // from dataframe to dataset
  val peopleDS = df.as[Person]
  peopleDS.show()
  // +----+-------+
  // | age|   name|
  // +----+-------+
  // |null|Michael|
  // |  30|   Andy|
  // |  19| Justin|
  // +----+-------+


  // Create an RDD
  val peopleRDD = spark.sparkContext.textFile(path.replace("json", "txt"))

  // Inferring the Schema Using Reflection
  val reflection_peopleDF = peopleRDD
    .map(_.split(","))
    .map(attributes => Person(attributes(0), attributes(1).trim.toInt))
    .toDF()
  reflection_peopleDF.show()

  // The schema is encoded in a string, created by Programmatically
  val schemaString = "name age"
  // Generate the schema based on the string of schema
  val fields = schemaString.split(" ")
    .map(fieldName => StructField(fieldName, StringType, nullable = true))
  val schema = StructType(fields)
  val rowRDD = peopleRDD
    .map(_.split(","))
    .map(attributes => Row(attributes(0), attributes(1).trim))
  val programmatically_peopleDF = spark.createDataFrame(rowRDD, schema)
  programmatically_peopleDF.createOrReplaceTempView("people")
  spark.sql("SELECT name FROM people").map(attributes => "Name: " + attributes(0)).show()
  // +-------------+
  // |        value|
  // +-------------+
  // |Name: Michael|
  // |   Name: Andy|
  // | Name: Justin|
  // +-------------+
}