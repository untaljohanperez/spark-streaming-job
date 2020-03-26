package config

object MongodbConfig {
  trait Service {
    def getMongodbConfig: MongoConfig
  }
}

trait MongodbConfig {
  def mongo: MongodbConfig.Service
}

trait MongodbConfigLive extends MongodbConfig {
  override def mongo: MongodbConfig.Service =
    new MongodbConfig.Service {
      def getMongodbConfig: MongoConfig = MongoConfig("mongodb://127.0.0.1/twitter-endava.tweets2")
    }
}

object MongodbConfigLive extends MongodbConfigLive



