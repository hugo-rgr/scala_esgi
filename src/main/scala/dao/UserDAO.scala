package dao
import Utils.DBConnection
import models.User
import scala.io.StdIn

object UserDAO {

  def userConnexion(): User = {
    println("|Connexion")

    println("Identifiant :")
    val identifiant = StdIn.readLine()
    println("Mot de passe:")
    val password = StdIn.readLine()

    val requete = "SELECT * FROM USER WHERE user_name = ?"

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

    println("Choisissez votre identifiant :")
    identifiant = StdIn.readLine()

    var passwordOk = false
    while (!passwordOk) {
      println("Choisissez votre mot de passe:")
      password = StdIn.readLine()
      println("Confirmez votre mot de passe")
      if (password == StdIn.readLine())
        passwordOk = true
      else
        println("/!\\Les mots de passe ne correspondent pas")
    }

    println("Vehicule")
    vehicule = StdIn.readLine()

    val requeteInsert = "INSERT INTO BLABLACAR.`User`" +
      "(user_name, user_hash_pwd, user_vehicule, user_note, user_nb_notes)" +
      "VALUES(?, ?, ?, 0, 0);"

    val statementInsert = DBConnection.connection.prepareStatement(requeteInsert)
    statementInsert.setString(1, identifiant)
    statementInsert.setString(2, password)
    statementInsert.setString(3, vehicule)
    statementInsert.executeUpdate()

    val requeteSelect = "SELECT * FROM USER WHERE user_name = ?"

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
    else {
      null
    }
  }

  def gestionCompte(userId: Int): Int = {
    println("|Mon compte")
    val menu = List("Modifier le mot de passe", "Modifier le nom d'utilisateur", "Modifier le vehicule", "Supprimer le compte", "Quitter")

    for ((section, index) <- menu.zipWithIndex) {
      println(s"${index + 1} : $section")
    }

    val choix = StdIn.readInt()

    choix match {
      case 1 =>
        var password: String = null
        var passwordOk = false
        while (!passwordOk) {
          println("Choisissez votre nouveau mot de passe:")
          password = StdIn.readLine()
          println("Confirmez votre nouveau mot de passe")
          if (password == StdIn.readLine())
            passwordOk = true
          else
            println("/!\\Les mots de passe ne correspondent pas")
        }
        val requete = "UPDATE USER SET user_hash_pwd = ? WHERE user_id = ?"
        try {
          val statement = DBConnection.connection.prepareStatement(requete)
          statement.setString(1, password)
          statement.setInt(2, userId)
          statement.executeUpdate()
          println("///Mise à jour du mot de passe réussie ")
          userId
        }
        catch {
          case e: Exception => e.printStackTrace()
            println("Echec lors de la mise à jour du mot de passe")
            userId
        }
      case 2 =>
        println("Choisissez votre nouveau nom")
        var userName = StdIn.readLine()

        val requete = "UPDATE USER SET user_name = ? WHERE user_id = ?"
        try {
          val statement = DBConnection.connection.prepareStatement(requete)
          statement.setString(1, userName)
          statement.setInt(2, userId)
          statement.executeUpdate()
          println("///Mise à jour du nom réussie ")
          userId
        }
        catch {
          case e: Exception => e.printStackTrace()
            println("Echec lors de la mise à jour du nom")
            userId
        }
      case 3 =>
        println("Choisissez votre nouveu vehicule")
        var userName = StdIn.readLine()

        val requete = "UPDATE USER SET user_vehicule = ? WHERE user_id = ?"
        try {
          val statement = DBConnection.connection.prepareStatement(requete)
          statement.setString(1, userName)
          statement.setInt(2, userId)
          statement.executeUpdate()
          println("///Mise à jour du vehicule réussie")
          userId
        }
        catch {
          case e: Exception => e.printStackTrace()
            println("Echec lors de la mise à jour du vehicule")
            userId
        }
      case 4 =>
        println("Confirmer la suppression ?")
        println("1: Non")
        println("2: Oui")
        val choix = StdIn.readInt()
        choix match {
          case 1 =>
            println("Annulation de la suppression")
            userId
          case 2 =>
            try {
              val requete = "DELETE FROM USER WHERE user_id = ?"
              val statement = DBConnection.connection.prepareStatement(requete)
              statement.setInt(1,userId)
              statement.executeUpdate()
              println("Suppression du compte réussie")
              0
            }catch{
              case e: Exception => e.printStackTrace()
                println("Echec lors de la suppression du compte")
                userId
            }
        }
      case _ =>
        println("Commande invalide !")
        userId
    }

  }

}