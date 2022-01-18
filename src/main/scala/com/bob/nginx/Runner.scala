package com.bob.nginx

import com.bob.BaseApp
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.spark.rdd.{RDD, UnionRDD}
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.reflect.ClassTag

object Runner extends BaseApp {

  val batch = 5 //计算周期（秒）
  spark.sparkContext.getConf.setAppName("analise-nginx-log")
  val ssc = new StreamingContext(spark.sparkContext, Seconds(batch))
  val input = "file:///Users/wangxiang/Desktop/nginx-log"
  val lines = namedTextFileStream(ssc, input)

  // lines.count().print()
  // lines.map(line => (line.split(" ")(0), 1)).reduceByKey(_ + _).print()

  val transformed = lines
    .transform(rdd => transformByFile(rdd, byFileTransformer))

  transformed.count().print()
  transformed.map(x => {
    println(x._1 + " --->" + x._2)
    (x._2.split(" ")(0), 1)
  }).reduceByKey(_ + _).print()

  ssc.start()
  ssc.awaitTermination()

  println("nginx log stoped")

  def namedTextFileStream(ssc: StreamingContext, directory: String): DStream[String] =
    ssc.fileStream[LongWritable, Text, TextInputFormat](directory)
      .transform(rdd =>
        new UnionRDD(rdd.context,
          rdd.dependencies.map(dep =>
            dep.rdd.asInstanceOf[RDD[(LongWritable, Text)]].map(_._2.toString).setName(dep.rdd.name)
          )
        )
      )

  def byFileTransformer(filename: String)(rdd: RDD[String]): RDD[(String, String)] = rdd.map(line => (filename, line))

  def transformByFile[U: ClassTag](unionrdd: RDD[String],
                                   transformFunc: String => RDD[String] => RDD[U]): RDD[U] = {
    new UnionRDD(unionrdd.context,
      unionrdd.dependencies.map { dep =>
        if (dep.rdd.isEmpty) None
        else {
          val filename = dep.rdd.name
          Some(
            transformFunc(filename)(dep.rdd.asInstanceOf[RDD[String]])
              .setName(filename)
          )
        }
      }.flatten
    )
  }
}