package config

import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import zio.Task



object SparkConfig {
  trait Service {
    def getSparkSession(mongoConfig: MongoConfig): Task[SparkSession]
    def getSparkStreamingContext(spark: SparkSession): Task[StreamingContext]
  }
}

trait SparkConfig {
  def spark: SparkConfig.Service
}

trait SparkConfigLive extends SparkConfig {
  def spark: SparkConfig.Service =
    new SparkConfig.Service {
      def getSparkSession(mongoConfig: MongoConfig): Task[SparkSession] =
        Task.effect(SparkSession.builder
          .appName("Simple Application").master("local[*]")
          .config("spark.streaming.kafka.allowNonConsecutiveOffsets", "true")
          .config("spark.mongodb.output.uri", mongoConfig.uri)
          .getOrCreate())

      def getSparkStreamingContext(spark: SparkSession): Task[StreamingContext] =
        Task.effect(new StreamingContext(spark.sparkContext, Seconds(1)))
    }
}

object SparkConfigLive extends SparkConfigLive


