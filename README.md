## spark 分析股市 && mllib 学习
> 在Spark2.0中，spark.mllib包中的RDD接口已进入维护模式。现在主要的机器学习接口为spark.ml包中的基于数据框接口。

#### 从新浪获取某只股票最新价格

* http://hq.sinajs.cn/list=sz002230,sh601007
> 上面可以获取深圳证券的科大讯飞跟上海证券的方大炭素两只股票价格

#### 写个类继承Receiver 
> [Spark Streaming Custom Receivers](http://spark.apache.org/docs/latest/streaming-custom-receivers.html)
>
> 上面只是从Url获取一个数据,这里因为Spark是被动接受该数据源进行分析,所以我们需要一个将上面的数据主动推送的过程,就是这个Receiver的作用

#### Launcher组装
> 现在只是简单的打印出

#### spark.ml
> Spark 2.0后,将会慢慢废弃掉 RDD-based 的Api(spark.mllib), 转向基于 DataFrame-based 的Api(spark.ml package)
* 机器学习算法 常规包括分类,回归,聚类和协同过滤
* 特征工程 特征抽取,转换,选择及降维
* 管道 构造,评估和调整

#### spark.graphx
> 图处理框架,基于Spark平台提供对图计算和图挖掘简洁易用的而丰富接口,方便了对分布式图处理需求
* 社交网络人跟人之间的关系链

#### spark.sql
[sql 基本使用](https://github.com/bobxwang/predict-stock-in-spark/blob/master/src/main/scala/com/bob/sql/SSql.scala)

#### breeze visual
[scala 图形库](https://github.com/scalanlp/breeze/)