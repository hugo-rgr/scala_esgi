package menu

object MessageMenu
{

  import Utils.DBConnection
  import models.User
  import scala.io.StdIn

  def display(): Unit = {
    var user: User = null
    var continue = true

    val statement = DBConnection.connection

    while (continue) {
      if (user == null) {
        println("|Menu d'authentification")
        println("1: Envoyer un message")
        println("2: Messages reçus")
        println("3: Retour au menu")

        println("Selectionnez une action")
        val choix = StdIn.readInt()

        choix match {
          case 1 =>
            user = dao.userInscription()
            if (user == null)
              println("/!\\ Erreur lors de l'inscription")
            else
              println("///Inscription reussie")
          case 2 =>
            user = dao.userConnexion()
            if (user == null)
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
        val menu = List("Rechercher un trajet", "Mes trajets", "Mes messages", "Mon compte", "Quitter")

        println("Menu principal :")
        for ((section, index) <- menu.zipWithIndex) {
          println(s"${index + 1} : $section")
        }

        println("Selectionnez une action")
        val choix = StdIn.readInt()

        choix match {
          case 1 =>
            println("Utilisateur")

          case 2 =>
            println("Reservation")

          case 3 =>
            println("Messagerie")
        }
      }
    }


  }

}