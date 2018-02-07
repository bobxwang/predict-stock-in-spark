package com.bob.mlib

import org.apache.spark.sql.SparkSession

trait BaseApp extends App {

  /**
    * Spark 2.0 后, SparkConf, SparkContext, SQLContext都已经被封装在SaprkSession中
    */
  val spark = SparkSession.builder()
    .master("local[4]")
    .appName("SinaStock-CustomReceiver")
    .config("spark.some.config.option", "config-value")
    .getOrCreate()
  spark.conf.set("spark.executor.memory", "2g")
}