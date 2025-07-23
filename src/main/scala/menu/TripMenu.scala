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

    // Trajets en tant que conducteur - récupérés depuis Trip
    val trajetsAVenirConducteur = TripDAO.findAll().filter(t =>
      t.tripDriverUserId == user.userId && t.tripDate.isAfter(maintenant)
    ).sortBy(_.tripDate)

    // Trajets en tant que passager - récupérés depuis Reservation
    val reservationsAVenir = ReservationDAO.findByPassengerUserId(user.userId).filter { reservation =>
      TripDAO.find(reservation.tripId) match {
        case Some(trip) => trip.tripDate.isAfter(maintenant)
        case None => false
      }
    }.sortBy { reservation =>
      TripDAO.find(reservation.tripId).get.tripDate
    }

    var continuer = true
    while (continuer) {
      println("\n| Trajets à venir")
      println("0. Retour à mes trajets")

      var index = 1

      // Afficher trajets conducteur
      if (trajetsAVenirConducteur.nonEmpty) {
        println("\n--- En tant que conducteur ---")
        trajetsAVenirConducteur.foreach { trajet =>
          val villeDepart = CityDAO.find(trajet.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          println(s"$index. $villeDepart - $villeArrivee ($dateFormatee) [CONDUCTEUR]")
          index += 1
        }
      }

      // Afficher trajets passager
      if (reservationsAVenir.nonEmpty) {
        println("\n--- En tant que passager ---")
        reservationsAVenir.foreach { reservation =>
          val trajet = TripDAO.find(reservation.tripId).get
          val villeDepart = CityDAO.find(trajet.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
          // Trajet conducteur
          afficherDetailTrajetConducteur(trajetsAVenirConducteur(choixAjuste), user, estPasse = false)
        } else {
          // Trajet passager - utiliser la réservation
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

    // Trajets passés en tant que conducteur - récupérés depuis Trip
    val trajetsPasseesConducteur = TripDAO.findAll().filter(t =>
      t.tripDriverUserId == user.userId && t.tripDate.isBefore(maintenant)
    ).sortBy(_.tripDate)(Ordering[LocalDateTime].reverse)

    // Trajets passés en tant que passager - récupérés depuis Reservation
    val reservationsPassees = ReservationDAO.findByPassengerUserId(user.userId).filter { reservation =>
      TripDAO.find(reservation.tripId) match {
        case Some(trip) => trip.tripDate.isBefore(maintenant)
        case None => false
      }
    }.sortBy { reservation =>
      TripDAO.find(reservation.tripId).get.tripDate
    }(Ordering[LocalDateTime].reverse)

    var continuer = true
    while (continuer) {
      println("\n| Trajets passés")
      println("0. Retour à mes trajets")

      var index = 1

      // Afficher trajets conducteur
      if (trajetsPasseesConducteur.nonEmpty) {
        println("\n--- En tant que conducteur ---")
        trajetsPasseesConducteur.foreach { trajet =>
          val villeDepart = CityDAO.find(trajet.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
          println(s"$index. $villeDepart - $villeArrivee ($dateFormatee) [CONDUCTEUR]")
          index += 1
        }
      }

      // Afficher trajets passager
      if (reservationsPassees.nonEmpty) {
        println("\n--- En tant que passager ---")
        reservationsPassees.foreach { reservation =>
          val trajet = TripDAO.find(reservation.tripId).get
          val villeDepart = CityDAO.find(trajet.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
          val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
          val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
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
          // Trajet conducteur
          afficherDetailTrajetConducteur(trajetsPasseesConducteur(choixAjuste), user, estPasse = true)
        } else {
          // Trajet passager - utiliser la réservation
          val indexPassager = choixAjuste - trajetsPasseesConducteur.length
          val reservation = reservationsPassees(indexPassager)
          afficherDetailTrajetPassager(reservation, user, estPasse = true)
        }
      } else {
        println("Choix invalide !")
      }
    }
  }

  private def afficherDetailTrajetConducteur(trajet: Trip, user: User, estPasse: Boolean): Unit = {
    val villeDepart = CityDAO.find(trajet.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
    val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
    val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val heureFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("HH'h'mm"))

    // Récupérer les réservations pour ce trajet
    val reservations = ReservationDAO.findByTripId(trajet.tripId)
    val passagersAvecReservations = reservations.map { reservation =>
      val passager = UserDAO.find(reservation.resPassengerUserId).getOrElse(
        User(userId = 0, nom = "Inconnu", vehicule = "Inconnu")
      )
      (passager, reservation)
    }

    println(s"\n| Détail d'un trajet")
    println(s"| $villeDepart - $villeArrivee")
    println(s"| Conducteur : vous")
    println(s"| Départ : $dateFormatee $heureFormatee")
    println(s"| Véhicule : ${user.vehicule}")
    println(s"| Prix : ${trajet.tripPrice} €")

    // Afficher les passagers avec leurs notes
    passagersAvecReservations.zipWithIndex.foreach { case ((passager, reservation), index) =>
      val notePassager = if (passager.nombreNote > 0)
        f"${passager.note.toDouble / passager.nombreNote}%.1f"
      else "Pas de note"
      println(s"| Passager ${index + 1} : ${passager.nom} ($notePassager)")
    }

    val placesRestantes = trajet.tripPassengersSeatsNumber - reservations.length
    println(s"| $placesRestantes places restantes")

    // Options
    println("0. Retour")
    if (estPasse && passagersAvecReservations.nonEmpty) {
      // Vérifier si toutes les réservations ont été notées
      val reservationsNonNotees = reservations.filter(res => res.resIsRated.isEmpty || !res.resIsRated.get)
      if (reservationsNonNotees.nonEmpty) {
        println("1. Noter les passagers")
      }
    }
    if (!estPasse) {
      println("2. Supprimer le trajet")
    }

    println("\nSelectionnez une action")
    val choix = StdIn.readInt()

    choix match {
      case 0 => // Retour
      case 1 if estPasse && passagersAvecReservations.nonEmpty =>
        noterPassagers(reservations, passagersAvecReservations.map(_._1))
      case 2 if !estPasse =>
        supprimerTrajet(trajet)
      case _ => println("Choix invalide !")
    }
  }

  private def afficherDetailTrajetPassager(reservation: Reservation, user: User, estPasse: Boolean): Unit = {
    // Récupérer le trajet depuis la réservation
    val trajet = TripDAO.find(reservation.tripId).get

    val villeDepart = CityDAO.find(trajet.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
    val villeArrivee = CityDAO.find(trajet.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
    val dateFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val heureFormatee = trajet.tripDate.format(DateTimeFormatter.ofPattern("HH'h'mm"))

    // Récupérer les informations du conducteur
    val conducteur = UserDAO.find(trajet.tripDriverUserId).getOrElse(
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

    val toutesReservations = ReservationDAO.findByTripId(trajet.tripId)
    val placesRestantes = trajet.tripPassengersSeatsNumber - toutesReservations.length
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
        noterConducteur(conducteur, reservation)
      case 2 if !estPasse =>
        annulerReservation(reservation)
      case _ => println("Choix invalide !")
    }
  }

  private def noterConducteur(conducteur: User, reservation: Reservation): Unit = {
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

      // Marquer cette réservation spécifique comme notée
      ReservationDAO.updateIsRated(reservation.resId, Some(true))

      println("/// Note prise en compte !")

    } catch {
      case _: NumberFormatException =>
        println("/!\\ Note invalide")
      case e: Exception =>
        println(s"/!\\ Erreur lors de la notation: ${e.getMessage}")
    }
  }

  private def noterPassagers(reservations: List[Reservation], passagers: List[User]): Unit = {
    println("\n| Noter les passagers")

    try {
      // Associer chaque passager à sa réservation
      val passagersAvecReservations = passagers.map { passager =>
        val reservation = reservations.find(_.resPassengerUserId == passager.userId).get
        (passager, reservation)
      }

      // Noter seulement les passagers dont la réservation n'a pas encore été notée
      val passagersANoter = passagersAvecReservations.filter { case (_, reservation) =>
        reservation.resIsRated.isEmpty || !reservation.resIsRated.get
      }

      if (passagersANoter.isEmpty) {
        println("Tous les passagers ont déjà été notés")
        return
      }

      val notesPassagers = passagersANoter.map { case (passager, reservation) =>
        println(s"Renseignez votre note pour ${passager.nom} (sur 5) : ")
        val note = StdIn.readDouble()
        if (note < 0 || note > 5) {
          throw new IllegalArgumentException("La note doit être entre 0 et 5")
        }
        (passager, reservation, (note * 10).toInt)
      }

      // Mettre à jour toutes les notes
      notesPassagers.foreach { case (passager, reservation, noteEntiere) =>
        val nouvelleNoteTotal = passager.note + noteEntiere
        val nouveauNombreNotes = passager.nombreNote + 1

        val passagerMisAJour = passager.copy(
          note = nouvelleNoteTotal,
          nombreNote = nouveauNombreNotes
        )

        UserDAO.update(passagerMisAJour)

        // Marquer la réservation spécifique comme notée
        ReservationDAO.updateIsRated(reservation.resId, Some(true))
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

  private def supprimerTrajet(trajet: Trip): Unit = {
    println("Êtes-vous sûr de vouloir supprimer ce trajet ? (oui/non)")
    val confirmation = StdIn.readLine().trim.toLowerCase

    if (confirmation == "oui" || confirmation == "o") {
      try {
        // Supprimer d'abord toutes les réservations associées
        val reservations = ReservationDAO.findByTripId(trajet.tripId)
        reservations.foreach(res => ReservationDAO.delete(res.resId))

        // Puis supprimer le trajet
        TripDAO.delete(trajet.tripId)
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