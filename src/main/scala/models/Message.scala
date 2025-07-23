package models

import java.time.LocalDateTime

case class Message(
                 id: Int,
                 content: String,
                 senderuid: Int,
                 recipientuid: Int,
                 date: LocalDateTime
)
