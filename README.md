## spark 分析股市

#### 从新浪获取某只股票最新价格

* http://hq.sinajs.cn/list=sz002230,sh601007
> 上面可以获取深圳证券的科大讯飞跟上海证券的方大炭素两只股票价格

#### 写个类继承Receiver 
> [Spark Streaming Custom Receivers](http://spark.apache.org/docs/latest/streaming-custom-receivers.html)
>
> 上面只是从Url获取一个数据,这里因为Spark是被动接受该数据源进行分析,所以我们需要一个将上面的数据主动推送的过程,就是这个Receiver的作用

#### Launcher组装
> 现在只是简单的打印出