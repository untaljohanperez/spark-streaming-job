package consumer

import config.KafkaConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent

object SparkConsumer {
  trait Service {
    def getInputDStream(streamingContext: StreamingContext, kafkaConfig: KafkaConfig): InputDStream[ConsumerRecord[String, String]]
  }
}

trait SparkConsumer {
  def consumer: SparkConsumer.Service
}

trait SparkConsumerLive extends SparkConsumer {
  def consumer: SparkConsumer.Service =
    (streamingContext: StreamingContext, kafkaConfig: KafkaConfig) => {
      KafkaUtils.createDirectStream[String, String](
        streamingContext,
        PreferConsistent,
        Subscribe[String, String](kafkaConfig.topics, kafkaConfig.kafkaParams)
      )
    }
}

object SparkConsumerLive extends SparkConsumerLive
