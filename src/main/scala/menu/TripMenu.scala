package menu

import dao.{TripDAO, CityDAO, ReservationDAO, UserDAO}
import models.{User, Trip, City, Reservation}
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
          val reservations = ReservationDAO.findByTripId(trajet.tripId)
          val nbPassagers = reservations.length
          println(s"${index + 1}. $villeDepart → $villeArrivee ($dateFormatee) - $nbPassagers passager(s)")
        }

        println("\nSelectionnez une action")
        val choix = StdIn.readInt()

        if (choix == 0) {
          continuer = false
        } else if (choix > 0 && choix <= trajetsAVenir.length) {
          afficherDetailTrajet(trajetsAVenir(choix - 1), user, false)
        } else {
          println("Choix invalide !")
        }
      }
    }
  }

  private def afficherTrajetsPassees(user: User): Unit = {
    var continuer = true
    while (continuer) {
      // Move data fetching inside the loop
      val maintenant = LocalDateTime.now()
      val tousLesTrajets = TripDAO.findAll()
      val trajetsPassees = tousLesTrajets.filter(t =>
        t.tripDriverUserId == user.userId && t.tripDate.isBefore(maintenant)
      ).sortBy(_.tripDate)(Ordering[LocalDateTime].reverse)

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
          val reservations = ReservationDAO.findByTripId(trajet.tripId)
          val nbPassagers = reservations.length
          println(s"${index + 1}. $villeDepart → $villeArrivee ($dateFormatee) - $nbPassagers passager(s)")
        }

        println("\nSelectionnez une action")
        val choix = StdIn.readInt()

        if (choix == 0) {
          continuer = false
        } else if (choix > 0 && choix <= trajetsPassees.length) {
          afficherDetailTrajet(trajetsPassees(choix - 1), user, true)
          // After returning from detail, the loop will refresh the data
        } else {
          println("Choix invalide !")
        }
      }
    }
  }

  private def afficherDetailTrajet(trajet: Trip, user: User, estPasse: Boolean): Unit = {
    val villeDepart = CityDAO.find(trajet.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
    val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
    val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val heureFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("HH'h'mm"))
    val reservations = ReservationDAO.findByTripId(trajet.tripId)

    // Fetch the actual driver information from database
    val conducteur = UserDAO.userFindById(trajet.tripDriverUserId).getOrElse(user)

    println(s"\n| Détail du trajet")
    println(s"| $villeDepart → $villeArrivee")
    println(s"| Conducteur : ${conducteur.nom} (${if (conducteur.nombreNote > 0) conducteur.note.formatted("%.1f") else "Pas de note"}/5)")
    println(s"| Départ : $dateFormatee $heureFormatee")
    println(s"| Véhicule : ${conducteur.vehicule}")
    println(s"| Prix : ${trajet.tripPrice} €")
    println(s"| ${trajet.tripPassengersSeatsNumber} places restantes")

    if (reservations.nonEmpty) {
      println("\n| Passagers :")
      reservations.zipWithIndex.foreach { case (reservation, index) =>
        UserDAO.userFindById(reservation.resPassengerUserId) match {
          case Some(passager) =>
            val noteStatus = if (estPasse) {
              reservation.resIsRated match {
                case Some(true) => "(Noté)"
                case Some(false) => "(Non noté)"
                case None => "(Non noté)"
              }
            } else ""
            println(s"${index + 1}. ${passager.nom} - ${reservation.resPassengerTripPrice}€ $noteStatus")
          case None =>
            println(s"${index + 1}. Passager introuvable")
        }
      }
    } else {
      println("\n| Aucun passager pour ce trajet")
    }

    if (estPasse && reservations.nonEmpty) {
      println("\n0. Retour")
      println("1. Noter les passagers")

      println("\nSelectionnez une action")
      val choix = StdIn.readInt()

      if (choix == 1) {
        noterPassagers(trajet, reservations)
      }
    } else {
      println("\n0. Retour")
      println("\nSelectionnez une action")
      val choix = StdIn.readInt()
    }
  }

  private def noterPassagers(trajet: Trip, reservations: List[Reservation]): Unit = {
    var continuer = true

    while (continuer) {
      // Refresh reservation data each time to get updated resIsRated values
      val reservationsActuelles = ReservationDAO.findByTripId(trajet.tripId)
      val passagersNonNotes = reservationsActuelles.filter(r => r.resIsRated.isEmpty || r.resIsRated.contains(false))

      println("\n| Noter les passagers")

      if (passagersNonNotes.isEmpty) {
        println("Tous les passagers ont déjà été notés")
        println("Appuyez sur Entrée pour continuer...")
        StdIn.readLine()
        continuer = false
      } else {
        println("Passagers à noter :")
        println("0. Retour")

        passagersNonNotes.zipWithIndex.foreach { case (reservation, index) =>
          UserDAO.userFindById(reservation.resPassengerUserId) match {
            case Some(passager) =>
              println(s"${index + 1}. ${passager.nom}")
            case None =>
              println(s"${index + 1}. Passager introuvable")
          }
        }

        println("\nSelectionnez le passager à noter :")
        val choix = StdIn.readInt()

        if (choix == 0) {
          continuer = false
        } else if (choix > 0 && choix <= passagersNonNotes.length) {
          val reservation = passagersNonNotes(choix - 1)
          UserDAO.userFindById(reservation.resPassengerUserId) match {
            case Some(passager) =>
              noterPassager(reservation, passager)
            // After rating, the loop will refresh the data automatically
            case None =>
              println("Passager introuvable")
          }
        } else {
          println("Choix invalide !")
        }
      }
    }
  }

  private def noterPassager(reservation: Reservation, passager: User): Unit = {
    println(s"\n| Noter le passager ${passager.nom}")
    println("Donnez une note de 1 à 5 étoiles :")

    var continuer = true
    while (continuer) {
      try {
        val note = StdIn.readInt()
        if (note >= 1 && note <= 5) {
          // Mettre à jour la note du passager
          if (UserDAO.noterUtilisateur(passager.userId, note)) {
            // Marquer la réservation comme notée
            ReservationDAO.updateIsRated(reservation.resId, Some(true))
            println(s"✓ Vous avez donné $note étoile(s) au passager ${passager.nom}")
            continuer = false
          } else {
            println("/!\\ Erreur lors de la notation")
            continuer = false
          }
        } else {
          println("Veuillez donner une note entre 1 et 5")
        }
      } catch {
        case _: NumberFormatException =>
          println("Veuillez entrer un nombre valide")
      }
    }
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