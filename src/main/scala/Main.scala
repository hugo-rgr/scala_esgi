import Utils.DBConnection
import models.User
import dao.UserDAO
import scala.io.StdIn
import menu.TripMenu

@main
def main(): Unit = {
  var user: User = null
  var continue = true

  val statement = DBConnection.connection

  while (continue) {
    if (user == null) {
      println("|Menu d'authentification")
      println("1: Inscription")
      println("2: Connexion")
      println("3: Quitter")

      println("Selectionnez une action")
      val choix = StdIn.readInt()

      choix match {
        case 1 =>
          user = UserDAO.userInscription()
          if(user == null)
            println("/!\\ Erreur lors de l'inscription")
          else
            println("///Inscription reussie")
        case 2 =>
          user = UserDAO.userConnexion()
          if(user == null)
            println("/!\\ Nom d'utilisateur ou mots de passe incorect ")
          else
            println("///Connexion reussie")
        case 3 =>
          println("A bientôt")
          continue = false
        case _ =>
          println("Commande invalide !")
      }
    }
    else {
      val menu = List("Rechercher un trajet", "Mes trajets", "Mes messages","Mon compte","Quitter")

      println("Menu principal :")
      for ((section, index) <- menu.zipWithIndex) {
        println(s"${index + 1} : $section")
      }

      println("Selectionnez une action")
      val choix = StdIn.readInt()

      choix match {
        case 1 =>
          println("Recherche de trajet - Fonctionnalité à implémenter")

        case 2 =>
          TripMenu.afficherMenu(user)

        case 3 =>
          println("Messagerie - Fonctionnalité à implémenter")

        case 4 =>
          println("Mon compte - Fonctionnalité à implémenter")

        case 5 =>
          println("A bientôt")
          continue = false

        case _ =>
          println("Commande invalide !")
      }
    }
  }
}