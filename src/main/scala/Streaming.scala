import config._
import model._
import twitter4j.TwitterObjectFactory
import com.mongodb.spark.sql._

object Streaming {

  def main(args: Array[String]): Unit = {

    val config = ConfigurationLive.config

    val mongoConfig = config.mongo.getMongodbConfig
    val spark = config.spark.getSparkSession(mongoConfig)
    val streamingContext = config.spark.getSparkStreamingContext(spark)
    val kafkaConfig = config.kafka.getKafkaConfig
    val stream = config.consumer.getInputDStream(streamingContext, kafkaConfig)

    val twitterEvents = stream.map(record => record.value)
      .map(record => {
        val status = TwitterObjectFactory.createStatus(record)

        TwitterEvent(status.getUser.getScreenName,
          new java.sql.Date(status.getCreatedAt.getTime),
          System.currentTimeMillis(),
          status.getHashtagEntities.map(hashTag => hashTag.getText).toSeq
        )
      })

    twitterEvents.foreachRDD({rdd =>
      import spark.implicits._
      rdd.toDF().write.mode("overwrite").mongo()
    })

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}