package config

import org.apache.kafka.common.serialization.StringDeserializer

trait KafkaConsumerConfiguration {

  def getKafkaConfig(): KafkaConfig = {
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
