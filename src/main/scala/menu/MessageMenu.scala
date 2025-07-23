package menu

object MessageMenu
{

  import Utils.DBConnection
  import models.User
  import scala.io.StdIn

  def afficherMenu(user: User): Unit = {
    var continuer = true

    while (continuer) {
      println("\n| Mes trajets")
      println("1. Nouveau trajet (conducteur)")
      println("2. Trajets à venir")
      println("3. Trajets passés")
      println("4. Retour au menu principal")

      println("\nSelectionnez une action")
      val choix = StdIn.readInt()

      choix match {
        case 1 => ecrireMessage(user)
        case 2 => afficherMessages(user)
        case 3 => chercherMessage(user)
        case 4 => continuer = false
        case _ => println("Commande invalide !")
      }
    }
  }
}