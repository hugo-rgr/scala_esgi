package menu

import dao.{TripDAO, CityDAO}
import models.{User, Trip, City}
import scala.io.StdIn
import java.time.{LocalDateTime, LocalDate, LocalTime}
import java.time.format.DateTimeFormatter
import java.math.BigDecimal

object TripMenu {

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
        case 1 => creerNouveauTrajet(user)
        case 2 => afficherTrajetsAVenir(user)
        case 3 => afficherTrajetsPassees(user)
        case 4 => continuer = false
        case _ => println("Commande invalide !")
      }
    }
  }

  private def creerNouveauTrajet(user: User): Unit = {
    println("\n| Nouveau trajet")

    try {
      // Saisie ville de départ
      println("Renseignez la ville de départ : ")
      val villeDepart = StdIn.readLine().trim
      val cityDepartId = obtenirOuCreerVille(villeDepart)

      // Saisie ville d'arrivée
      println("Renseignez la ville d'arrivée : ")
      val villeArrivee = StdIn.readLine().trim
      val cityArriveeId = obtenirOuCreerVille(villeArrivee)

      // Saisie date
      println("Renseignez la date de départ (AAAA-MM-JJ) : ")
      val dateStr = StdIn.readLine().trim
      val date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"))

      // Saisie heure
      println("Renseignez l'heure de départ (HH:MM) : ")
      val heureStr = StdIn.readLine().trim
      val heure = LocalTime.parse(heureStr, DateTimeFormatter.ofPattern("HH:mm"))

      val dateTime = LocalDateTime.of(date, heure)

      // Saisie nombre de places
      println("Renseignez le nombre de places : ")
      val nbPlaces = StdIn.readInt()

      // Saisie prix
      println("Renseignez le prix par passager en euros : ")
      val prix = BigDecimal.valueOf(StdIn.readDouble())

      // Création du trajet
      val nouveauTrajet = Trip(
        tripId = 0, // sera généré par la base
        tripDepartureCityId = cityDepartId,
        tripArrivalCityId = cityArriveeId,
        tripDate = dateTime,
        tripDriverUserId = user.userId,
        tripPassengersSeatsNumber = nbPlaces,
        tripPrice = prix
      )

      TripDAO.insert(nouveauTrajet)
      println("✓ Trajet créé avec succès !")

    } catch {
      case e: Exception =>
        println(s"/!\\ Erreur lors de la création du trajet: ${e.getMessage}")
    }
  }

  private def afficherTrajetsAVenir(user: User): Unit = {
    val maintenant = LocalDateTime.now()
    val tousLesTrajets = TripDAO.findAll()
    val trajetsAVenir = tousLesTrajets.filter(t =>
      t.tripDriverUserId == user.userId && t.tripDate.isAfter(maintenant)
    ).sortBy(_.tripDate)

    var continuer = true
    while (continuer) {
      println("\n| Trajets à venir")
      println("0. Retour à mes trajets")

      if (trajetsAVenir.isEmpty) {
        println("Aucun trajet à venir")
        println("\nSelectionnez une action")
        val choix = StdIn.readInt()
        if (choix == 0) continuer = false
      } else {
        trajetsAVenir.zipWithIndex.foreach { case (trajet, index) =>
          val villeDepart = CityDAO.find(trajet.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          println(s"${index + 1}. $villeDepart - $villeArrivee ($dateFormatee)")
        }

        println("\nSelectionnez une action")
        val choix = StdIn.readInt()

        if (choix == 0) {
          continuer = false
        } else if (choix > 0 && choix <= trajetsAVenir.length) {
          afficherDetailTrajet(trajetsAVenir(choix - 1), user)
        } else {
          println("Choix invalide !")
        }
      }
    }
  }

  private def afficherTrajetsPassees(user: User): Unit = {
    val maintenant = LocalDateTime.now()
    val tousLesTrajets = TripDAO.findAll()
    val trajetsPassees = tousLesTrajets.filter(t =>
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
    }
  }

  private def afficherDetailTrajet(trajet: Trip, user: User): Unit = {
    val villeDepart = CityDAO.find(trajet.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
    val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
    val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val heureFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("HH'h'mm"))

    println(s"\n| Détail d'un trajet")
    println(s"| $villeDepart - $villeArrivee")
    println(s"| Conducteur : ${user.nom} (${if (user.nombreNote > 0) (user.note.toDouble / user.nombreNote) else "Pas de note"})")
    println(s"| Départ : $dateFormatee $heureFormatee")
    println(s"| Véhicule : ${user.vehicule}")
    println(s"| Prix : ${trajet.tripPrice} € - payé") // Statut simplifié
    println(s"| ${trajet.tripPassengersSeatsNumber} places restantes")
    println("0. Retour")

    println("\nSelectionnez une action")
    val choix = StdIn.readInt()
    // Pour l'instant, on retourne juste au menu précédent
  }

  private def obtenirOuCreerVille(nomVille: String): Int = {
    val villesExistantes = CityDAO.findAll()
    villesExistantes.find(_.cityName.equalsIgnoreCase(nomVille)) match {
      case Some(ville) => ville.cityId
      case None =>
        val nouvelleVille = City(cityId = 0, cityName = nomVille)
        CityDAO.insert(nouvelleVille)
        // Récupérer l'ID de la ville créée
        CityDAO.findAll().find(_.cityName.equalsIgnoreCase(nomVille)).get.cityId
    }
  }
}