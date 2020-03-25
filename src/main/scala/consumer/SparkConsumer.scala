package consumer

import config.KafkaConfig
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

trait SparkConsumer {
  def getInputDStream(streamingContext: StreamingContext, kafkaConfig: KafkaConfig) = {
    KafkaUtils.createDirectStream[String, String](
      streamingContext,
      PreferConsistent,
      Subscribe[String, String](kafkaConfig.topics, kafkaConfig.kafkaParams)
    )
  }
}
