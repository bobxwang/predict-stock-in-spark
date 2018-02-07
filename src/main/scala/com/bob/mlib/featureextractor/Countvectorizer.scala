package com.bob.mlib.featureextractor

import com.bob.BaseApp
import org.apache.spark.ml.feature.{CountVectorizer, CountVectorizerModel}

/**
  * Countvectorizer和Countvectorizermodel旨在通过计数来将一个文档转换为向量。
  * 当不存在先验字典时，Countvectorizer可作为Estimator来提取词汇，并生成一个Countvectorizermodel。
  * 该模型产生文档关于词语的稀疏表示，其表示可以传递给其他算法如LDA。
  */
object Countvectorizer extends BaseApp {

  val df = spark.createDataFrame(Seq(
    (0, Array("a", "b", "c")),
    (1, Array("a", "b", "b", "c", "a"))
  )).toDF("id", "words")

  // fit a CountVectorizerModel from the corpus
  val cvModel: CountVectorizerModel = new CountVectorizer()
    .setInputCol("words")
    .setOutputCol("features")
    .setVocabSize(3)
    .setMinDF(2)
    .fit(df)

  // alternatively, define CountVectorizerModel with a-priori vocabulary
  val cvm = new CountVectorizerModel(Array("a", "b", "c"))
    .setInputCol("words")
    .setOutputCol("features")

  cvModel.transform(df).select("features").show()
}