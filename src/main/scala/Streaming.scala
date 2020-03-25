import com.mongodb.spark.MongoSpark
import config._
import org.apache.spark.streaming.StreamingContext
import model._
import consumer._
import twitter4j.TwitterObjectFactory
import com.mongodb.spark.sql._

object Streaming extends KafkaConsumerConfiguration with SparkConfiguration with SparkConsumer {

  def main(args: Array[String]): Unit = {

    val spark = getSparkSession
    val streamingContext: StreamingContext = getSparkStreamingContext(spark)
    val kafkaConfig = getKafkaConfig
    val stream = getInputDStream(streamingContext, kafkaConfig)

    val twitterEvents = stream.map(record => record.value)
      .map(record => {
        val status = TwitterObjectFactory.createStatus(record)

        TwitterEvent(status.getUser.getScreenName,
          new java.sql.Date(status.getCreatedAt.getTime),
          System.currentTimeMillis(),
          status.getHashtagEntities.map(hashTag => hashTag.getText).toSeq
        )
      })

    //def counter

    twitterEvents.cache()
    twitterEvents.map(_.author).print(20)

    twitterEvents.foreachRDD({rdd =>
      import spark.implicits._
      rdd.toDF().write.mode("overwrite").mongo()
    })

    streamingContext.start()
    streamingContext.awaitTermination()
  }
}