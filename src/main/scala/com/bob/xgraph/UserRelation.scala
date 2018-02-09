package com.bob.xgraph

import com.bob.BaseApp
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD

/**
  * 参照 resources 下面的 UserRelation.png 图片
  */
object UserRelation extends BaseApp {

  val vertexArray = Array(
    (1L, ("Alice", 28)),
    (2L, ("Bob", 27)),
    (3L, ("Charlie", 65)),
    (4L, ("David", 42)),
    (5L, ("Ed", 55)),
    (6L, ("Fran", 50))
  )

  val edgeArray = Array(
    Edge(2L, 1L, 7),
    Edge(2L, 4L, 2),
    Edge(3L, 2L, 4),
    Edge(3L, 6L, 3),
    Edge(4L, 1L, 1),
    Edge(5L, 2L, 2),
    Edge(5L, 3L, 8),
    Edge(5L, 6L, 3)
  )

  val vertexRDD: RDD[(Long, (String, Int))] = spark.sparkContext.parallelize(vertexArray)
  val edgeRDD: RDD[Edge[Int]] = spark.sparkContext.parallelize(edgeArray)
  val graph: Graph[(String, Int), Int] = Graph(vertexRDD, edgeRDD)

  println("打印出图中年龄大于30的顶点")
  graph.vertices.filter {
    case (_, (_, age)) => age > 30
  }.collect().foreach(x => println(s"id is ${x._1} and name is ${x._2._1} with age is ${x._2._2}"))
  //  id is 4 and name is David with age is 42
  //  id is 5 and name is Ed with age is 55
  //  id is 6 and name is Fran with age is 50
  //  id is 3 and name is Charlie with age is 65

  println("打印出图中属性大于5的边")
  graph.edges.filter(x => x.attr > 5)
    .collect().foreach(x => println(s"${x.srcId} to ${x.dstId} att ${x.attr}"))
  //  2 to 1 att 7
  //  5 to 3 att 8

  println("打印出图中边属性大于5的")
  graph.triplets.filter(x => x.attr > 5).collect().foreach(x => println(s"${x.srcAttr._1} likes ${x.dstAttr._1}"))
  // Bob likes Alice
  // Ed likes Charlie

  def max(a: (VertexId, Int), b: (VertexId, Int)) = if (a._2 > b._2) a else b

  println("打印出图中最大的出度,入度,度数")
  println("max of outDegrees: " + graph.outDegrees.reduce(max)
    + " max of inDegrees: " + graph.inDegrees.reduce(max)
    + " max of Degrees: " + graph.degrees.reduce(max))
  // max of outDegrees: (5,3) max of inDegrees: (2,2) max of Degrees: (2,4)
}