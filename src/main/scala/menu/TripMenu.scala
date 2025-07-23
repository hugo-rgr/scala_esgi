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

    // CONDUCTEUR: Récupérer les trajets depuis Trip où je suis conducteur
    val trajetsAVenirConducteur = TripDAO.findAll().filter(trip =>
      trip.tripDriverUserId == user.userId && trip.tripDate.isAfter(maintenant)
    ).sortBy(_.tripDate)

    // PASSAGER: Récupérer les trajets depuis Reservation où je suis passager
    val reservationsAVenir = ReservationDAO.findByPassengerUserId(user.userId).filter { reservation =>
      !reservation.resIsCanceled &&
        TripDAO.find(reservation.tripId).exists(_.tripDate.isAfter(maintenant))
    }.sortBy(res => TripDAO.find(res.tripId).get.tripDate)

    var continuer = true
    while (continuer) {
      println("\n| Trajets à venir")
      println("0. Retour à mes trajets")

      var index = 1

      // Afficher trajets CONDUCTEUR
      if (trajetsAVenirConducteur.nonEmpty) {
        println("\n--- En tant que conducteur ---")
        trajetsAVenirConducteur.foreach { trip =>
          val villeDepart = CityDAO.find(trip.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trip.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
          println(s"$index. $villeDepart - $villeArrivee ($dateFormatee) [CONDUCTEUR]")
          index += 1
        }
      }

      // Afficher trajets PASSAGER
      if (reservationsAVenir.nonEmpty) {
        println("\n--- En tant que passager ---")
        reservationsAVenir.foreach { reservation =>
          val trip = TripDAO.find(reservation.tripId).get
          val villeDepart = CityDAO.find(trip.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trip.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
          println(s"$index. $villeDepart - $villeArrivee ($dateFormatee) [PASSAGER]")
          index += 1
        }
      }

      if (trajetsAVenirConducteur.isEmpty && reservationsAVenir.isEmpty) {
        println("Aucun trajet à venir")
      }

      println("\nSelectionnez une action")
      val choix = StdIn.readInt()

      if (choix == 0) {
        continuer = false
      } else if (choix > 0 && choix < index) {
        val choixAjuste = choix - 1
        if (choixAjuste < trajetsAVenirConducteur.length) {
          // Trajet CONDUCTEUR - géré depuis Trip
          afficherDetailTrajetConducteur(trajetsAVenirConducteur(choixAjuste), user, estPasse = false)
        } else {
          // Trajet PASSAGER - géré depuis Reservation
          val indexPassager = choixAjuste - trajetsAVenirConducteur.length
          val reservation = reservationsAVenir(indexPassager)
          afficherDetailTrajetPassager(reservation, user, estPasse = false)
        }
      } else {
        println("Choix invalide !")
      }
    }
  }

  private def afficherTrajetsPassees(user: User): Unit = {
    val maintenant = LocalDateTime.now()

    // CONDUCTEUR: Récupérer les trajets depuis Trip où je suis conducteur
    val trajetsPasseesConducteur = TripDAO.findAll().filter(trip =>
      trip.tripDriverUserId == user.userId && trip.tripDate.isBefore(maintenant)
    ).sortBy(_.tripDate)(Ordering[LocalDateTime].reverse)

    // PASSAGER: Récupérer les trajets depuis Reservation où je suis passager
    val reservationsPassees = ReservationDAO.findByPassengerUserId(user.userId).filter { reservation =>
      !reservation.resIsCanceled &&
        TripDAO.find(reservation.tripId).exists(_.tripDate.isBefore(maintenant))
    }.sortBy(res => TripDAO.find(res.tripId).get.tripDate)(Ordering[LocalDateTime].reverse)

    var continuer = true
    while (continuer) {
      println("\n| Trajets passés")
      println("0. Retour à mes trajets")

      var index = 1

      // Afficher trajets CONDUCTEUR
      if (trajetsPasseesConducteur.nonEmpty) {
        println("\n--- En tant que conducteur ---")
        trajetsPasseesConducteur.foreach { trip =>
          val villeDepart = CityDAO.find(trip.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trip.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
          println(s"$index. $villeDepart - $villeArrivee ($dateFormatee) [CONDUCTEUR]")
          index += 1
        }
      }

      // Afficher trajets PASSAGER
      if (reservationsPassees.nonEmpty) {
        println("\n--- En tant que passager ---")
        reservationsPassees.foreach { reservation =>
          val trip = TripDAO.find(reservation.tripId).get
          val villeDepart = CityDAO.find(trip.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trip.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
          println(s"$index. $villeDepart - $villeArrivee ($dateFormatee) [PASSAGER]")
          index += 1
        }
      }

      if (trajetsPasseesConducteur.isEmpty && reservationsPassees.isEmpty) {
        println("Aucun trajet passé")
      }

      println("\nSelectionnez une action")
      val choix = StdIn.readInt()

      if (choix == 0) {
        continuer = false
      } else if (choix > 0 && choix < index) {
        val choixAjuste = choix - 1
        if (choixAjuste < trajetsPasseesConducteur.length) {
          // Trajet CONDUCTEUR - géré depuis Trip
          afficherDetailTrajetConducteur(trajetsPasseesConducteur(choixAjuste), user, estPasse = true)
        } else {
          // Trajet PASSAGER - géré depuis Reservation
          val indexPassager = choixAjuste - trajetsPasseesConducteur.length
          val reservation = reservationsPassees(indexPassager)
          afficherDetailTrajetPassager(reservation, user, estPasse = true)
        }
      } else {
        println("Choix invalide !")
      }
    }
  }

  // GESTION CONDUCTEUR - basée sur Trip
  private def afficherDetailTrajetConducteur(trip: Trip, user: User, estPasse: Boolean): Unit = {
    val villeDepart = CityDAO.find(trip.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
    val villeArrivee = CityDAO.find(trip.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
    val dateFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val heureFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("HH'h'mm"))

    // Récupérer les réservations pour ce trajet
    val reservations = ReservationDAO.findByTripId(trip.tripId)
    val passagers = reservations.map(res => UserDAO.find(res.resPassengerUserId)).collect { case Some(u) => u }

    println(s"\n| Détail d'un trajet")
    println(s"| $villeDepart - $villeArrivee")
    println(s"| Conducteur : vous")
    println(s"| Départ : $dateFormatee $heureFormatee")
    println(s"| Véhicule : ${user.vehicule}")
    println(s"| Prix : ${trip.tripPrice} €")

    // Afficher les passagers
    passagers.zipWithIndex.foreach { case (passager, index) =>
      val notePassager = if (passager.nombreNote > 0)
        f"${passager.note.toDouble / passager.nombreNote}%.1f"
      else "Pas de note"
      println(s"| Passager ${index + 1} : ${passager.nom} ($notePassager)")
    }

    val placesRestantes = trip.tripPassengersSeatsNumber - reservations.length
    println(s"| $placesRestantes places restantes")

    // Options
    println("0. Retour")
    if (estPasse && passagers.nonEmpty) {
      println("1. Noter les passagers")
    }
    if (!estPasse) {
      println("2. Supprimer le trajet")
    }

    println("\nSelectionnez une action")
    val choix = StdIn.readInt()

    choix match {
      case 0 => // Retour
      case 1 if estPasse && passagers.nonEmpty =>
        noterPassagersDuTrip(trip, reservations, passagers)
      case 2 if !estPasse =>
        supprimerTrajet(trip)
      case _ => println("Choix invalide !")
    }
  }

  // GESTION PASSAGER - basée sur Reservation
  private def afficherDetailTrajetPassager(reservation: Reservation, user: User, estPasse: Boolean): Unit = {
    val trip = TripDAO.find(reservation.tripId).get
    val villeDepart = CityDAO.find(trip.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
    val villeArrivee = CityDAO.find(trip.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
    val dateFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val heureFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("HH'h'mm"))

    // Récupérer les informations du conducteur
    val conducteur = UserDAO.find(trip.tripDriverUserId).getOrElse(
      User(userId = 0, nom = "Inconnu", vehicule = "Inconnu")
    )
    val noteConducteur = if (conducteur.nombreNote > 0)
      f"${conducteur.note.toDouble / conducteur.nombreNote}%.1f"
    else "Pas de note"

    println(s"\n| Détail d'un trajet")
    println(s"| $villeDepart - $villeArrivee")
    println(s"| Conducteur : ${conducteur.nom} ($noteConducteur)")
    println(s"| Départ : $dateFormatee $heureFormatee")
    println(s"| Véhicule : ${conducteur.vehicule}")
    println(s"| Prix : ${reservation.resPassengerTripPrice} € - payé")

    val reservations = ReservationDAO.findByTripId(trip.tripId)
    val placesRestantes = trip.tripPassengersSeatsNumber - reservations.length
    println(s"| $placesRestantes places restantes")

    // Options
    println("0. Retour")
    if (estPasse && (reservation.resIsRated.isEmpty || !reservation.resIsRated.get)) {
      println("1. Noter le chauffeur")
    }
    if (!estPasse) {
      println("2. Annuler la réservation")
    }

    println("\nSelectionnez une action")
    val choix = StdIn.readInt()

    choix match {
      case 0 => // Retour
      case 1 if estPasse && (reservation.resIsRated.isEmpty || !reservation.resIsRated.get) =>
        noterConducteurDepuisReservation(conducteur, reservation)
      case 2 if !estPasse =>
        annulerReservation(reservation)
      case _ => println("Choix invalide !")
    }
  }

  private def noterConducteurDepuisReservation(conducteur: User, reservation: Reservation): Unit = {
    println(s"\n| Noter ${conducteur.nom}")
    println("Renseignez votre note (sur 5) : ")

    try {
      val note = StdIn.readDouble()
      if (note < 0 || note > 5) {
        println("/!\\ La note doit être entre 0 et 5")
        return
      }

      // Convertir la note en entier (multiplier par 10 pour garder une décimale)
      val noteEntiere = (note * 10).toInt

      // Mettre à jour la note du conducteur
      val nouvelleNoteTotal = conducteur.note + noteEntiere
      val nouveauNombreNotes = conducteur.nombreNote + 1

      val conducteurMisAJour = conducteur.copy(
        note = nouvelleNoteTotal,
        nombreNote = nouveauNombreNotes
      )

      UserDAO.update(conducteurMisAJour)

      // Marquer la réservation comme notée
      ReservationDAO.updateIsRated(reservation.resId, Some(true))

      println("/// Note prise en compte !")

    } catch {
      case _: NumberFormatException =>
        println("/!\\ Note invalide")
      case e: Exception =>
        println(s"/!\\ Erreur lors de la notation: ${e.getMessage}")
    }
  }

  private def noterPassagersDuTrip(trip: Trip, reservations: List[Reservation], passagers: List[User]): Unit = {
    println("\n| Noter les passagers")

    try {
      val notesPassagers = passagers.map { passager =>
        println(s"Renseignez votre note pour ${passager.nom} (sur 5) : ")
        val note = StdIn.readDouble()
        if (note < 0 || note > 5) {
          throw new IllegalArgumentException("La note doit être entre 0 et 5")
        }
        (passager, (note * 10).toInt)
      }

      // Mettre à jour toutes les notes
      notesPassagers.foreach { case (passager, noteEntiere) =>
        val nouvelleNoteTotal = passager.note + noteEntiere
        val nouveauNombreNotes = passager.nombreNote + 1

        val passagerMisAJour = passager.copy(
          note = nouvelleNoteTotal,
          nombreNote = nouveauNombreNotes
        )

        UserDAO.update(passagerMisAJour)

        // Marquer la réservation correspondante comme notée
        reservations.find(_.resPassengerUserId == passager.userId).foreach { reservation =>
          ReservationDAO.updateIsRated(reservation.resId, Some(true))
        }
      }

      println("/// Notes prises en compte !")

    } catch {
      case _: NumberFormatException =>
        println("/!\\ Note invalide")
      case e: IllegalArgumentException =>
        println(s"/!\\ ${e.getMessage}")
      case e: Exception =>
        println(s"/!\\ Erreur lors de la notation: ${e.getMessage}")
    }
  }

  private def supprimerTrajet(trip: Trip): Unit = {
    println("Êtes-vous sûr de vouloir supprimer ce trajet ? (oui/non)")
    val confirmation = StdIn.readLine().trim.toLowerCase

    if (confirmation == "oui" || confirmation == "o") {
      try {
        // Supprimer d'abord toutes les réservations associées
        val reservations = ReservationDAO.findByTripId(trip.tripId)
        reservations.foreach(res => ReservationDAO.delete(res.resId))

        // Puis supprimer le trajet
        TripDAO.delete(trip.tripId)
        println("✓ Trajet supprimé avec succès !")
      } catch {
        case e: Exception =>
          println(s"/!\\ Erreur lors de la suppression: ${e.getMessage}")
      }
    }
  }

  private def annulerReservation(reservation: Reservation): Unit = {
    println("Êtes-vous sûr de vouloir annuler cette réservation ? (oui/non)")
    val confirmation = StdIn.readLine().trim.toLowerCase

    if (confirmation == "oui" || confirmation == "o") {
      try {
        ReservationDAO.updateIsCancelled(reservation.resId, Some(true))
        println("✓ Réservation annulée avec succès !")
      } catch {
        case e: Exception =>
          println(s"/!\\ Erreur lors de l'annulation: ${e.getMessage}")
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