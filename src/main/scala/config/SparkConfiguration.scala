package config

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.{Seconds, StreamingContext}



object SparkConfig {
  trait Service {
    def getSparkSession(mongoConfig: MongoConfig): SparkSession
    def getSparkStreamingContext(spark: SparkSession): StreamingContext
  }
}

trait SparkConfig {
  def spark: SparkConfig.Service
}

trait SparkConfigLive extends SparkConfig {
  def spark: SparkConfig.Service =
    new SparkConfig.Service {
      def getSparkSession(mongoConfig: MongoConfig): SparkSession = {
        SparkSession.builder
          .appName("Simple Application").master("local[*]")
          .config("spark.streaming.kafka.allowNonConsecutiveOffsets", "true")
          .config("spark.mongodb.output.uri", mongoConfig.uri)
          .getOrCreate()
      }

      def getSparkStreamingContext(spark: SparkSession) =
        new StreamingContext(spark.sparkContext, Seconds(1))
}

object SparkConfigLive extends SparkConfigLive

