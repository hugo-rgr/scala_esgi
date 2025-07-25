import Utils.DBConnection
import models.User
import dao.UserDAO
import menu.{MessageMenu, TripMenu, TripSearchMenu, UserMenu, ReservationMenu}

import scala.io.StdIn  // Import du nouveau module

@main
def main(): Unit = {
  var user: User = null
  var continue = true

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
          user = UserMenu.inscription()
          if(user==null)
            println("/!\\ Erreur lors de l'inscription")
          else
            println("///Inscription reussie")
        case 2 =>
          user = UserMenu.connexion()
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
      val menu = List("Rechercher un trajet", "Mes trajets", "Mes réservations", "Mes messages","Mon compte","Quitter")

      println("Menu principal :")
      for ((section, index) <- menu.zipWithIndex) {
        println(s"${index + 1} : $section")
      }

      println("Selectionnez une action")
      val choix = StdIn.readInt()

      choix match {
        case 1 =>
          TripSearchMenu.display(user)

        case 2 =>
          TripMenu.afficherMenu(user)

        case 3 =>
          ReservationMenu.afficherMenu(user)

        case 4 =>
          MessageMenu.afficherMenu(user)

        case 5 =>
          val result = UserMenu.gestionCompte(user)
          if(result == null)
            user = null
        case 6 =>
          println("A bientôt")
          continue = false

        case _ =>
          println("Commande invalide !")
      }
    }
  }
}