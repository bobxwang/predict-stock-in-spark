package com.bob.predict

import com.bob.predict.spark.SinaStockReceiver
import com.bob.predict.stock.SinaStock
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.util.Random

/**
  * Created by wangxiang on 18/2/7.
  */
object Launcher extends App {

  /* reduce从左到右进行折叠。其实就是先处理t-6，t-5的RDD，将结果与t-4的RDD再次调用reduceFunc，依次类推直到当前RDD */
  def reduceFunc(left: (Float, Int), right: (Float, Int)): (Float, Int) = {
    println("left " + left + "right " + right)
    (right._1, left._2 + right._2)
  }

  val sparkConf = new SparkConf().setAppName("SinaStock-CustomReceiver").setMaster("local[4]")
  val ssc = new StreamingContext(sparkConf, Seconds(1))
  ssc.checkpoint("./tmp")
  val lines = ssc.receiverStream(new SinaStockReceiver())
  val words = lines.map(SinaStock(_))
  val stockState = words.map(
    sinaStock => (sinaStock.name, (sinaStock.curPrice + Random.nextFloat, -1))).filter(stock => !stock._1.isEmpty)

  /* 每3秒，处理过去6秒的数据，对数据进行变化的累加 */
  val stockTrend = stockState.reduceByKeyAndWindow(reduceFunc(_, _), Seconds(6), Seconds(3))

  stockState.print()
  stockTrend.print()

  ssc.start()
  ssc.awaitTermination()
  println("StockTrend")
}