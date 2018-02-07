package com.bob.mlib

import org.apache.spark.ml.feature.Word2Vec

/**
  * Word2vec是一个Estimator，它采用一系列代表文档的词语来训练word2vecmodel
  */
object WordTwoVec extends BaseApp {

  // Input data: Each row is a bag of words from a sentence or document.
  val documentDF = spark.createDataFrame(Seq(
    "Hi I heard about Spark".split(" "),
    "I wish Java could use case classes".split(" "),
    "Logistic regression models are neat".split(" ")
  ).map(Tuple1.apply)).toDF("text")

  // Learn a mapping from words to Vectors.
  val word2Vec = new Word2Vec()
    .setInputCol("text")
    .setOutputCol("result")
    .setVectorSize(3)
    .setMinCount(0)
  val model = word2Vec.fit(documentDF)
  val result = model.transform(documentDF)
  result.select("result").take(3).foreach(println)
}