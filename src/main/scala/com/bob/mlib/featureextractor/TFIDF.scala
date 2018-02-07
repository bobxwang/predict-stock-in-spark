package com.bob.mlib.featureextractor

import com.bob.BaseApp
import org.apache.spark.ml.feature.{HashingTF, IDF, Tokenizer}

/**
  * 词频－逆向文件频率（TF-IDF）是一种在文本挖掘中广泛使用的特征向量化方法，它可以体现一个文档中词语在语料库中的重要程度。
  */
object TFIDF extends BaseApp {

  val sentenceData = spark.createDataFrame(Seq(
    (0, "Hi I heard about Spark"),
    (0, "I wish Java could use case classes"),
    (1, "Logistic regression models are neat")
  )).toDF("label", "sentence")

  // Tokenizer将文本划分为独立个体属分词器
  val tokenizer = new Tokenizer().setInputCol("sentence").setOutputCol("words")
  val wordsData = tokenizer.transform(sentenceData)
  val hashingTF = new HashingTF()
    .setInputCol("words").setOutputCol("rawFeatures").setNumFeatures(20)
  val featurizedData = hashingTF.transform(wordsData)
  // alternatively, CountVectorizer can also be used to get term frequency vectors

  val idf = new IDF().setInputCol("rawFeatures").setOutputCol("features")
  val idfModel = idf.fit(featurizedData)
  val rescaledData = idfModel.transform(featurizedData)
  rescaledData.select("features", "label").take(3).foreach(println)
}

/**
  * RegexTokenizer 基于正则表达式提供更多的划分选项
  * new RegexTokenizer().setPattern("\\W")  // alternatively .setPattern("\\w+").setGaps(false)
  *
  * StopWordsRemover 停用词为在文档中频繁出现，但未承载太多意义的词语，他们不应该被包含在算法输入中。
  */