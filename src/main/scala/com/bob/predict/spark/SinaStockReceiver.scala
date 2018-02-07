package com.bob.predict.spark

import org.apache.spark.internal.Logging
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.receiver.Receiver

import scala.io.Source

/**
  * Created by wangxiang on 18/2/7.
  */
class SinaStockReceiver(list: List[String]) extends Receiver[String](StorageLevel.MEMORY_AND_DISK_2) with Logging {

  override def onStart(): Unit = {
    new Thread("SinaStock Receiver") {
      override def run(): Unit = receive()
    }.start()
  }

  override def onStop(): Unit = {}

  private def receive(): Unit = {
    try {
      while (!isStopped) {

        val s = list.map(x => if (x.startsWith("6")) s"sh${x}" else s"sz${x}").mkString(",")
        val url = s"http://hq.sinajs.cn/list=${s}"
        logInfo(url)
        val sinaStockStream = Source.fromURL(url, "gbk")
        val sinaLines = sinaStockStream.getLines
        for (line <- sinaLines) {
          logInfo(line)
          store(line)
        }
        sinaStockStream.close()
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