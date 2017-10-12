(import 'org.apache.spark.api.java.JavaSparkContext)
(import 'org.apache.spark.api.java.JavaRDD)
(import 'org.apache.spark.SparkConf)


(def conf
  (doto (SparkConf.)
  (.setAppName "MiSpark")
  (.setMaster "local")))

(def sc (JavaSparkContext. conf))

(def distData (.parallelize sc [1 2 3 4 5]))
(.map distData identity)
(.take distData 10)



