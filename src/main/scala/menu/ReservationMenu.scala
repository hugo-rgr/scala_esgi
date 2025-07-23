package menu

import dao.{ReservationDAO, TripDAO, CityDAO, UserDAO}
import models.{User, Reservation, Trip}
import scala.io.StdIn
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ReservationMenu {

  def afficherMenu(user: User): Unit = {
    var continuer = true

    while (continuer) {
      println("\n| Mes réservations")
      println("1. Réservations à venir")
      println("2. Réservations passées")
      println("3. Retour au menu principal")

      println("\nSelectionnez une action")
      val choix = StdIn.readInt()

      choix match {
        case 1 => afficherReservationsAVenir(user)
        case 2 => afficherReservationsPassees(user)
        case 3 => continuer = false
        case _ => println("Commande invalide !")
      }
    }
  }

  private def afficherReservationsAVenir(user: User): Unit = {
    val maintenant = LocalDateTime.now()
    val reservations = ReservationDAO.findByPassengerUserId(user.userId)
    val reservationsAVenir = reservations.filter { reservation =>
      TripDAO.find(reservation.tripId) match {
        case Some(trip) => trip.tripDate.isAfter(maintenant)
        case None => false
      }
    }

    var continuer = true
    while (continuer) {
      println("\n| Réservations à venir")
      println("0. Retour aux réservations")

      if (reservationsAVenir.isEmpty) {
        println("Aucune réservation à venir")
        println("\nSelectionnez une action")
        val choix = StdIn.readInt()
        if (choix == 0) continuer = false
      } else {
        reservationsAVenir.zipWithIndex.foreach { case (reservation, index) =>
          TripDAO.find(reservation.tripId) match {
            case Some(trip) =>
              val villeDepart = CityDAO.find(trip.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
              val villeArrivee = CityDAO.find(trip.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
              val dateFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
              val conducteur = UserDAO.userFindById(trip.tripDriverUserId).map(_.nom).getOrElse("Inconnu")
              println(s"${index + 1}. $villeDepart → $villeArrivee ($dateFormatee) - Conducteur: $conducteur - ${reservation.resPassengerTripPrice}€")
            case None =>
              println(s"${index + 1}. Trajet introuvable")
          }
        }

        println("\nSelectionnez une action")
        val choix = StdIn.readInt()

        if (choix == 0) {
          continuer = false
        } else if (choix > 0 && choix <= reservationsAVenir.length) {
          afficherDetailReservation(reservationsAVenir(choix - 1), user, false)
        } else {
          println("Choix invalide !")
        }
      }
    }
  }

  private def afficherReservationsPassees(user: User): Unit = {
    val maintenant = LocalDateTime.now()
    val reservations = ReservationDAO.findByPassengerUserId(user.userId)
    val reservationsPassees = reservations.filter { reservation =>
      TripDAO.find(reservation.tripId) match {
        case Some(trip) => trip.tripDate.isBefore(maintenant)
        case None => false
      }
    }.sortBy(_.resDate)(Ordering[LocalDateTime].reverse)

    var continuer = true
    while (continuer) {
      println("\n| Réservations passées")
      println("0. Retour aux réservations")

      if (reservationsPassees.isEmpty) {
        println("Aucune réservation passée")
        println("\nSelectionnez une action")
        val choix = StdIn.readInt()
        if (choix == 0) continuer = false
      } else {
        reservationsPassees.zipWithIndex.foreach { case (reservation, index) =>
          TripDAO.find(reservation.tripId) match {
            case Some(trip) =>
              val villeDepart = CityDAO.find(trip.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
              val villeArrivee = CityDAO.find(trip.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
              val dateFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
              val conducteur = UserDAO.userFindById(trip.tripDriverUserId).map(_.nom).getOrElse("Inconnu")
              val noteStatus = reservation.resIsRated match {
                case Some(true) => "(Noté)"
                case Some(false) => "(Non noté)"
                case None => "(Non noté)"
              }
              println(s"${index + 1}. $villeDepart → $villeArrivee ($dateFormatee) - Conducteur: $conducteur - ${reservation.resPassengerTripPrice}€ $noteStatus")
            case None =>
              println(s"${index + 1}. Trajet introuvable")
          }
        }

        println("\nSelectionnez une action")
        val choix = StdIn.readInt()

        if (choix == 0) {
          continuer = false
        } else if (choix > 0 && choix <= reservationsPassees.length) {
          afficherDetailReservation(reservationsPassees(choix - 1), user, true)
        } else {
          println("Choix invalide !")
        }
      }
    }
  }

  private def afficherDetailReservation(reservation: Reservation, user: User, estPasse: Boolean): Unit = {
    TripDAO.find(reservation.tripId) match {
      case Some(trip) =>
        val villeDepart = CityDAO.find(trip.tripDepartureCityId).map(_.cityName).getOrElse("Inconnue")
        val villeArrivee = CityDAO.find(trip.tripArrivalCityId).map(_.cityName).getOrElse("Inconnue")
        val dateFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val heureFormatee = trip.tripDate.format(DateTimeFormatter.ofPattern("HH'h'mm"))

        UserDAO.userFindById(trip.tripDriverUserId) match {
          case Some(conducteur) =>
            println(s"\n| Détail de la réservation")
            println(s"| $villeDepart → $villeArrivee")
            println(s"| Conducteur : ${conducteur.nom} (${if (conducteur.nombreNote > 0) (conducteur.note.toDouble / conducteur.nombreNote).formatted("%.1f") else "Pas de note"}/5)")
            println(s"| Départ : $dateFormatee $heureFormatee")
            println(s"| Véhicule : ${conducteur.vehicule}")
            println(s"| Prix payé : ${reservation.resPassengerTripPrice} €")
            println(s"| Date de réservation : ${reservation.resDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))}")

            if (estPasse) {
              reservation.resIsRated match {
                case Some(true) =>
                  println("| Vous avez déjà noté ce conducteur")
                  println("0. Retour")
                case Some(false) | None =>
                  println("0. Retour")
                  println("1. Noter le conducteur")
                case _ =>
                  println("0. Retour")
              }

              println("\nSelectionnez une action")
              val choix = StdIn.readInt()

              if (choix == 1 && (reservation.resIsRated.isEmpty || reservation.resIsRated.contains(false))) {
                noterConducteur(reservation, conducteur)
              }
            } else {
              println("0. Retour")
              println("\nSelectionnez une action")
              StdIn.readInt()
            }

          case None =>
            println("Conducteur introuvable")
        }
      case None =>
        println("Trajet introuvable")
    }
  }

  private def noterConducteur(reservation: Reservation, conducteur: User): Unit = {
    println(s"\n| Noter le conducteur ${conducteur.nom}")
    println("Donnez une note de 1 à 5 étoiles :")

    var continuer = true
    while (continuer) {
      try {
        val note = StdIn.readInt()
        if (note >= 1 && note <= 5) {
          // Mettre à jour la note du conducteur
          if (UserDAO.noterUtilisateur(conducteur.userId, note)) {
            // Marquer la réservation comme notée
            ReservationDAO.updateIsRated(reservation.resId, Some(true))
            println(s"✓ Vous avez donné $note étoile(s) au conducteur ${conducteur.nom}")
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
}