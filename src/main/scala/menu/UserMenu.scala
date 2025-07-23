package menu

import dao.UserDAO
import models.User

import scala.io.StdIn

object UserMenu {

  def connexion(): User = {
    println("| Connexion")
    print("Identifiant : ")
    val identifiant = StdIn.readLine()
    print("Mot de passe : ")
    val password = StdIn.readLine()

    UserDAO.getUserByIdentifiant(identifiant) match {
      case user if UserDAO.checkPassword(user, password) =>
        println(s"Bienvenue ${user.nom} !")
        user
      case _ =>
        println("Identifiant ou mot de passe incorrect.")
        null
    }
  }

  def inscription(): User = {
    println("| Inscription")

    print("Choisissez un identifiant : ")
    val identifiant = StdIn.readLine()

    var passwordOk = false
    var password = ""
    while (!passwordOk) {
      print("Mot de passe : ")
      password = StdIn.readLine()
      print("Confirmez le mot de passe : ")
      if (password == StdIn.readLine()) passwordOk = true
      else println("/!\\Les mots de passe ne correspondent pas.")
    }

    print("Vehicule : ")
    val vehicule = StdIn.readLine()

    if (UserDAO.insertUser(identifiant, password, vehicule)) {
      println("Inscription réussie.")
      UserDAO.getUserByIdentifiant(identifiant)
    } else {
      println("Erreur lors de l'inscription.")
      null
    }
  }

  def gestionCompte(user: User): User = {
    println("| Mon compte")
    val menu = List(
      "Modifier le mot de passe",
      "Modifier le nom d'utilisateur",
      "Modifier le véhicule",
      "Supprimer le compte",
      "Quitter"
    )

    menu.zipWithIndex.foreach { case (section, index) =>
      println(s"${index + 1} : $section")
    }

    StdIn.readInt() match {
      case 1 =>
        println("Nouveau mot de passe : ")
        val nouveau = StdIn.readLine()
        println("Confirmation : ")
        if (nouveau == StdIn.readLine()) {
          if (UserDAO.updateChamp(user.userId, "user_hash_pwd", nouveau)) println("Mot de passe mis à jour.")
        } else println("Les mots de passe ne correspondent pas.")

        user

      case 2 =>
        print("Nouveau nom d'utilisateur : ")
        val nom = StdIn.readLine()
        if (UserDAO.updateChamp(user.userId, "user_name", nom)) {
          println("Nom mis à jour.")
          user.copy(nom = nom)
        } else user

      case 3 =>
        print("Nouveau véhicule : ")
        val vehicule = StdIn.readLine()
        if (UserDAO.updateChamp(user.userId, "user_vehicule", vehicule)) {
          println("Véhicule mis à jour.")
          user.copy(vehicule = vehicule)
        } else user

      case 4 =>
        println("Confirmer la suppression (1: Non / 2: Oui) : ")
        StdIn.readInt() match {
          case 2 =>
            if (UserDAO.deleteUser(user.userId)) {
              println("Compte supprimé.")
              null
            } else {
              println("Échec de la suppression.")
              user
            }
          case _ =>
            println("Suppression annulée.")
            user
        }

      case _ =>
        println("Retour au menu.")
        user
    }
  }
}
