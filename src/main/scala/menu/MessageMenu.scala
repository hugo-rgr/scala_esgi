package menu

import dao.{MessageDAO, UserDAO}
import models.Message

import java.time.LocalDateTime

object MessageMenu
{

  import Utils.DBConnection
  import models.User
  import scala.io.StdIn

  def afficherMenu(user: User): Unit = {
    var continuer = true

    while (continuer) {
      println("\n| Menu des messages")
      println("1. Rédiger message")
      println("2. Afficher messages reçus")
      println("3. Afficher messages envoyés")
      println("0. Retour au menu principal")

      println("\nSelectionnez une action")
      val choix = StdIn.readInt()

      choix match {
        case 1 => ecrireMessage(user)
        case 2 => afficherRecus(user)
        case 3 => afficherEnvoyes(user)
        case 0 => continuer = false
        case _ => println("Commande invalide !")
      }
    }
  }

  private def ecrireMessage(user: User): Unit = {
    println("\n| Rediger message")

    try {
      // Saisie ville de départ
      println("Choissisez le destinataire : ")
      val destinataire = StdIn.readLine().trim
      val destinataireId = obtenirDest(destinataire)

      // Saisie prix
      println("Rédigez votre message !")
      val msg = StdIn.readLine()

      // Création du trajet
      val message = Message(
        id = 0, // sera généré par la base
        content = msg,
        senderuid = user.userId,
        recipientuid = destinataireId,
        date = LocalDateTime.now()
      )

      MessageDAO.insert(message)
      println("✓ Le message a été rédigé !")

    } catch {
      case e: Exception =>
        println(s"/!\\ Erreur lors de la rédaction du msg ! ${e.getMessage}")
    }
  }

  private def afficherRecus(user: User): Unit = {
    val maintenant = LocalDateTime.now()
    val messages = MessageDAO.findReceived(user.userId)

    var continuer = true
    while (continuer) {
      println("\n| Trajets à venir")
      println("0. Retour à mes trajets")

      if (messages.isEmpty) {
        println("Aucun message reçu")
        println("\nSelectionnez une action")
        val choix = StdIn.readInt()
        if (choix == 0) continuer = false
      } else {
        messages.zipWithIndex.foreach { case (message, index) =>
          /*val villeDepart = MessageDAO.find(message.).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))*/
          //println(s"${index + 1}. $villeDepart - $villeArrivee ($dateFormatee)")
        }

        println("\nSelectionnez une action")
        val choix = StdIn.readInt()

        if (choix == 0) {
          continuer = false
        } else if (choix > 0 && choix <= messages.length) {
          //afficherDetailTrajet(messages(choix - 1), user)
        } else {
          println("Choix invalide !")
        }
      }
    }
  }

  private def afficherEnvoyes(user: User): Unit = {
    val maintenant = LocalDateTime.now()
    val tousLesTrajets = MessageDAO.findEcrits(user.userId)
    /*val trajetsPassees = tousLesTrajets.filter(t =>
      t.tripDriverUserId == user.userId && t.tripDate.isBefore(maintenant)
    ).sortBy(_.tripDate)(Ordering[LocalDateTime].reverse)

    var continuer = true
    while (continuer) {
      println("\n| Trajets passés")
      println("0. Retour à mes trajets")

      if (trajetsPassees.isEmpty) {
        println("Aucun trajet passé")
        println("\nSelectionnez une action")
        val choix = StdIn.readInt()
        if (choix == 0) continuer = false
      } else {
        trajetsPassees.zipWithIndex.foreach { case (trajet, index) =>
          val villeDepart = CityDAO.find(trajet.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          println(s"${index + 1}. $villeDepart - $villeArrivee ($dateFormatee)")
        }

        println("\nSelectionnez une action")
        val choix = StdIn.readInt()

        if (choix == 0) {
          continuer = false
        } else if (choix > 0 && choix <= trajetsPassees.length) {
          afficherDetailTrajet(trajetsPassees(choix - 1), user)
        } else {
          println("Choix invalide !")
        }
      }
    }*/
  }
  
  private def obtenirDest(nomUser: String): Int = {
    val users = UserDAO.findAll()
    users.find(_.nom.equalsIgnoreCase(nomUser)) match {
      case Some(user) => user.userId
    }
  }
}