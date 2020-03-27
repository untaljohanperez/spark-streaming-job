import config._
import model._
import twitter4j.TwitterObjectFactory
import com.mongodb.spark.sql._
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.DStream
import zio._
import zio.{Task, ZIO, App}

object TwitterProcessor extends App {

  def run(args: List[String]): UIO[Int] =
    twitterProcessor
      .provide(ConfigurationLive)
      .fold(_ => 1, _ => 0)

  def twitterProcessor: ZIO[Configuration, Throwable, Unit] = for {
    config <- ZIO.accessM[Configuration](_.config)
    mongoConfig <- config.mongo.getMongodbConfig
    spark <- config.spark.getSparkSession(mongoConfig)
    streamingContext <- config.spark.getSparkStreamingContext(spark)
    kafkaConfig <- config.kafka.getKafkaConfig
    stream <- config.consumer.getInputDStream(streamingContext, kafkaConfig)
    twitterEvents <- transformStream(stream)
    _ <- writeToMongo(twitterEvents, spark)
  } yield startStream(streamingContext)

  def startStream(streamingContext: StreamingContext): Task[Unit] =
    Task.effect {
      streamingContext.start()
      streamingContext.awaitTermination()
    }

  def writeToMongo(twitterEvents: DStream[TwitterEvent], spark: SparkSession): Task[Unit] =
    Task(twitterEvents.foreachRDD({rdd =>
      import spark.implicits._
      rdd.toDF().write.mode("overwrite").mongo()
    }))

  def transformStream(stream: DStream[ConsumerRecord[String, String]]): Task[DStream[TwitterEvent]] =
    Task {
      stream.map(record => record.value)
      .map(record => {
        val status = TwitterObjectFactory.createStatus(record)

        TwitterEvent(status.getUser.getScreenName,
          new java.sql.Date(status.getCreatedAt.getTime),
          System.currentTimeMillis(),
          status.getHashtagEntities.map(hashTag => hashTag.getText).toSeq
        )
      })
    }
}