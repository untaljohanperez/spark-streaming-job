package config

final case class KafkaConfig(kafkaParams: Map[String, Object], topics: Array[String])
