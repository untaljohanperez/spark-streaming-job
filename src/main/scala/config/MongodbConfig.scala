package config

import zio.Task


object MongodbConfig {
  trait Service {
    def getMongodbConfig: Task[MongoConfig]
  }
}

trait MongodbConfig {
  def mongo: MongodbConfig.Service
}

trait MongodbConfigLive extends MongodbConfig {
  override def mongo: MongodbConfig.Service =
    new MongodbConfig.Service {
      def getMongodbConfig: Task[MongoConfig] = Task.effect(MongoConfig("mongodb://127.0.0.1/twitter-endava.tweets2"))
    }
}

object MongodbConfigLive extends MongodbConfigLive



