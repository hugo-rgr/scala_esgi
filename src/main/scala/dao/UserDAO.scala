package dao

import Utils.DBConnection
import models.User
import scala.io.StdIn

object UserDAO {

  // Méthode pour trouver un utilisateur par ID
  def find(id: Int): Option[User] = {
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

  // Méthode pour récupérer tous les utilisateurs
  def findAll(): List[User] = {
    val requete = "SELECT * FROM User"
    val statement = DBConnection.connection.createStatement()
    val result = statement.executeQuery(requete)
    var users = List[User]()

    while (result.next()) {
      val user = User(
        userId = result.getInt("user_id"),
        nom = result.getString("user_name"),
        vehicule = result.getString("user_vehicule"),
        note = result.getInt("user_note"),
        nombreNote = result.getInt("user_nb_notes")
      )
      users = user :: users
    }
    users.reverse
  }

  // Méthode pour mettre à jour un utilisateur
  def update(user: User): Unit = {
    val requete = "UPDATE User SET user_name = ?, user_vehicule = ?, user_note = ?, user_nb_notes = ? WHERE user_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setString(1, user.nom)
    statement.setString(2, user.vehicule)
    statement.setInt(3, user.note)
    statement.setInt(4, user.nombreNote)
    statement.setInt(5, user.userId)
    statement.executeUpdate()
  }

  // Méthode pour supprimer un utilisateur
  def delete(id: Int): Unit = {
    val requete = "DELETE FROM User WHERE user_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, id)
    statement.executeUpdate()
  }

  // Méthodes existantes de connexion et inscription
  def userConnexion(): User = {
    println("|Connexion")

    println("Identifiant :")
    val identifiant = StdIn.readLine()
    println("Mot de passe:")
    val password = StdIn.readLine()

    val requete = "SELECT * FROM User WHERE user_name = ?"

    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setString(1, identifiant)

    val result = statement.executeQuery()

    if (result.next()) {
      if (result.getString("user_hash_pwd") == password) {
        val user = User(
          userId = result.getInt("user_id"),
          nom = result.getString("user_name"),
          vehicule = result.getString("user_vehicule"),
          note = result.getInt("user_note"),
          nombreNote = result.getInt("user_nb_notes")
        )
        user
      } else {
        null
      }
    } else {
      null
    }
  }

  def userInscription(): User = {
    println("|Inscription")
    var identifiant: String = null
    var password: String = null
    var vehicule: String = null

    println("Identifiant :")
    identifiant = StdIn.readLine()

    var passwordOk = false
    while(!passwordOk) {
      println("Mot de passe:")
      password = StdIn.readLine()
      println("Taper de nouveau le mots de passe")
      if (password == StdIn.readLine())
        passwordOk = true
    }

    println("Vehicule")
    vehicule = StdIn.readLine()

    val requeteInsert = "INSERT INTO User" +
      "(user_name, user_hash_pwd, user_vehicule, user_note, user_nb_notes)" +
      "VALUES(?, ?, ?, 0, 0);"

    val statementInsert = DBConnection.connection.prepareStatement(requeteInsert)
    statementInsert.setString(1,identifiant)
    statementInsert.setString(2,password)
    statementInsert.setString(3,vehicule)
    statementInsert.executeUpdate()

    val requeteSelect = "SELECT * FROM User WHERE user_name = ?"

    val statementSelect = DBConnection.connection.prepareStatement(requeteSelect)
    statementSelect.setString(1, identifiant)

    val result = statementSelect.executeQuery()

    if (result.next()) {
      val user = User(
        userId = result.getInt("user_id"),
        nom = result.getString("user_name"),
        vehicule = result.getString("user_vehicule"),
        note = result.getInt("user_note"),
        nombreNote = result.getInt("user_nb_notes")
      )
      user
    }
    else
    {
      null
    }
  }
}