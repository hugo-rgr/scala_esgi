package dao

import Utils.DBConnection
import models.User
import scala.io.StdIn

object UserDAO {

  def getUserByIdentifiant(identifiant: String): User = {
    val requete = "SELECT * FROM USER WHERE user_name = ?"
    
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setString(1, identifiant)

    val result = statement.executeQuery()

    if (result.next()) {
      User(
        userId = result.getInt("user_id"),
        nom = result.getString("user_name"),
        vehicule = result.getString("user_vehicule"),
        note = result.getInt("user_note"),
        nombreNote = result.getInt("user_nb_notes")
      )
    } else null
  }

  def checkPassword(user: User, password: String): Boolean = {
    val requete = "SELECT user_hash_pwd FROM USER WHERE user_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, user.userId)
    val result = statement.executeQuery()
    result.next() && result.getString("user_hash_pwd") == password
  }

  def insertUser(identifiant: String, password: String, vehicule: String): Boolean = {
    val requeteInsert =
      """INSERT INTO BLABLACAR.User (user_name, user_hash_pwd, user_vehicule, user_note, user_nb_notes)
        |VALUES (?, ?, ?, 0, 0)
        |""".stripMargin

    val statement = DBConnection.connection.prepareStatement(requeteInsert)
    statement.setString(1, identifiant)
    statement.setString(2, password)
    statement.setString(3, vehicule)

    statement.executeUpdate() > 0
  }

  def updateChamp(userId: Int, champ: String, valeur: String): Boolean = {
    val requete = s"UPDATE USER SET $champ = ? WHERE user_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setString(1, valeur)
    statement.setInt(2, userId)
    statement.executeUpdate() > 0
  }

  def deleteUser(userId: Int): Boolean = {
    val requete = "DELETE FROM USER WHERE user_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, userId)
    statement.executeUpdate() > 0
  }

  def userFindById(id: Int): Option[User] = {
    val requete = "SELECT * FROM User WHERE user_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, id)
    val result = statement.executeQuery()

    if (result.next()) {
      Some(User(
        userId = result.getInt("user_id"),
        nom = result.getString("user_name"),
        vehicule = result.getString("user_vehicule"),
        note = result.getInt("user_note"),
        nombreNote = result.getInt("user_nb_notes")
      ))
    } else {
      None
    }
  }
}
