package dao

import Utils.DBConnection
import models.Message

import java.sql.PreparedStatement
import java.sql.*
import java.time.LocalDateTime

object MessageDAO {
  def createTable(): Unit = {
    val requete =
      """CREATE TABLE Message (
        |    message_id INT PRIMARY KEY AUTO_INCREMENT,
        |    message_content VARCHAR(255) NOT NULL,
        |    sender_user_id INT NOT NULL,
        |    recipient_user_id INT NOT NULL,
        |    message_date DATETIME NOT NULL,
        |    FOREIGN KEY (sender_user_id) REFERENCES User(user_id),
        |    FOREIGN KEY (recipient_user_id) REFERENCES User(user_id)
        |);""".stripMargin

    val statement = DBConnection.connection.prepareStatement(requete)
    statement.execute()
  }

  def findAll(id: Int): List[Message] = {
    val requete = "SELECT * FROM Message WHERE sender_user_id = ? OR recipient_user_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, id)
    statement.setInt(2, id)
    val result = statement.executeQuery()
    var msgs = List[Message]()

    while (result.next()) {
      val msg = Message(
        id = result.getInt("message_id"),
        content = result.getString("message_content"),
        senderuid = result.getInt("sender_user_id"),
        recipientuid = result.getInt("recipient_user_id"),
        date = LocalDateTime.parse(result.getString("message_date"))
      )
      msgs = msg :: msgs
    }
    msgs.reverse
  }

  def find(uid: Int, id: Int): Option[Message] = {
    val requete = "SELECT * FROM Message WHERE sender_user_id = ? OR recipient_user_id = ? AND message_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, uid)
    statement.setInt(2, uid)
    statement.setInt(3, id)
    val result = statement.executeQuery()
    Option.when(result.next()) {
      Message(
        id = result.getInt("message_id"),
        content = result.getString("message_content"),
        senderuid = result.getInt("sender_user_id"),
        recipientuid = result.getInt("recipient_user_id"),
        date = LocalDateTime.parse(result.getString("message_date"))
      )
    }
  }

  def insert(msg: Message): Unit = {
    val requete = "INSERT INTO Message " +
      "(message_content, sender_user_id, recipient_user_id, message_date) " +
      "VALUES (?, ?, ?, ?)"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setString(1, msg.content)
    statement.setInt(2, msg.senderuid)
    statement.setInt(3, msg.recipientuid)
    statement.setString(4, LocalDateTime.now().toString)
    statement.executeUpdate()
  }
}
