package config

trait MongodbConfig {

  def getMongodbConfig: MongoConfig = MongoConfig("mongodb://127.0.0.1/twitter-endava.tweets2")
}
