package config

import org.apache.kafka.common.serialization.StringDeserializer
import zio.Task

object KafkaConsumerConfiguration {
  trait Service {
    def getKafkaConfig(): Task[KafkaConfig]
  }
}

trait KafkaConsumerConfiguration {
  def config: KafkaConsumerConfiguration.Service
}

trait KafkaConsumerConfigurationLive extends KafkaConsumerConfiguration {

  def config: KafkaConsumerConfiguration.Service =
    () => Task {
      val kafkaParams = Map[String, Object](
        "bootstrap.servers" -> "localhost:19092,localhost:19093,localhost:19094",
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "group.id" -> s"prices-nyse-group-${scala.util.Random.nextInt.toString}",
        "auto.offset.reset" -> "latest",
        "enable.auto.commit" -> (false: java.lang.Boolean)
      )
      val topics = Array("my_topic")

      KafkaConfig(kafkaParams, topics)
    }
}

object KafkaConsumerConfigurationLive extends KafkaConsumerConfigurationLive
