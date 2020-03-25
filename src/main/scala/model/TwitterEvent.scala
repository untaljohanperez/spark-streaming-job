package model

import java.sql.Date

final case class TwitterEvent(author: String, date: Date, transactionTime: Long, hashTags: Seq[String])