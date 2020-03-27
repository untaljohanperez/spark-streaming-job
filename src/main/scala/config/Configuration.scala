package config

import consumer._

import zio.Task

object Configuration {
  trait Service {
    def kafka: KafkaConsumerConfiguration.Service
    def spark: SparkConfig.Service
    def consumer: SparkConsumer.Service
    def mongo: MongodbConfig.Service
  }
}

trait Configuration {
  def config: Task[Configuration.Service]
}

trait ConfigurationLive extends Configuration {
  def config: Task[Configuration.Service] =
    Task(new Configuration.Service {
      override def kafka: KafkaConsumerConfiguration.Service = KafkaConsumerConfigurationLive.config

      override def spark: SparkConfig.Service = SparkConfigLive.spark

      override def consumer: SparkConsumer.Service = SparkConsumerLive.consumer

      override def mongo: MongodbConfig.Service = MongodbConfigLive.mongo
    })
}

object ConfigurationLive extends ConfigurationLive
