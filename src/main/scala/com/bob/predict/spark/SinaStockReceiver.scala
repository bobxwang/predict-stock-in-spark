package com.bob.predict.spark

import org.apache.spark.internal.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

import scala.io.Source

/**
  * Created by wangxiang on 18/2/7.
  */
class SinaStockReceiver extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) with Logging {

  override def onStart(): Unit = {
    new Thread("SinaStock Receiver") {
      override def run(): Unit = receive()
    }.start()
  }

  override def onStop(): Unit = { }

  private def receive(): Unit = {
    try {
      while (!isStopped) {
        var stockIndex = 1
        while (stockIndex != 0) {
          val stockCode = 601000 + stockIndex
          val url = "http://hq.sinajs.cn/list=sh%d".format(stockCode)
          logInfo(url)
          val sinaStockStream = Source.fromURL(url, "gbk")
          val sinaLines = sinaStockStream.getLines
          for (line <- sinaLines) {
            logInfo(line)
            store(line)
          }
          sinaStockStream.close()
          stockIndex = (stockIndex + 1) % 1
        }
      }
      logInfo("Stopped receiving")
      restart("Trying to connect again")
    } catch {
      case e: java.net.ConnectException =>
        restart("Error connecting to", e)
      case t: Throwable =>
        restart("Error receiving data", t)
    }
  }
}