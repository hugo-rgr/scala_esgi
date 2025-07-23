package menu

import dao.{CityDAO, ReservationDAO, TripDAO, UserDAO}
import models.{City, Reservation, Trip}

import java.time.LocalDate

object TripSearchMenu {

  def display(): Unit = {
    println("| Rechercher un trajet")

    println("Villes disponibles :")
    val cities = CityDAO.findAll()
    cities.foreach(city => println(s"${city.cityId}. ${city.cityName}"))
    var remainingCities: List[City] = List.empty

    print("Tapez le numero de la ville de depart : ")
    var continue = true
    var departureCityId = 0
    var departureCity: Option[City] = None
    while (continue) {
      departureCityId = scala.io.StdIn.readInt()
      departureCity = cities.find(_.cityId == departureCityId)

      if (departureCity.isDefined) {
        println(s"Vous avez choisi la ville de depart : ${departureCity.get.cityName}")
        var remainingCities = cities.filterNot(_.cityId == departureCityId)
        continue = false
      } else {
        println("Ville non valide, veuillez reessayer.")
      }
    }

    remainingCities.foreach(city => println(s"${city.cityId}. ${city.cityName}"))
    println("\nTapez le numero de la ville d'arrivee : ")
    continue = true
    var arrivalCityId = 0
    var arrivalCity: Option[City] = None
    while (continue) {
      arrivalCityId = scala.io.StdIn.readInt()
      arrivalCity = cities.find(_.cityId == arrivalCityId)

      if (arrivalCity.isDefined) {
        println(s"Vous avez choisi la ville d'arrivee : ${arrivalCity.get.cityName}")
        continue = false
      } else {
        println("Ville non valide, veuillez reessayer.")
      }
    }

    println("\nRenseignez la date du trajet (format AAAA-MM-JJ): ")
    continue = true
    var dateInput = ""
    while (continue) {
      dateInput = scala.io.StdIn.readLine()
      if (dateInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
        continue = false
      } else {
        println("Format de date invalide, veuillez reessayer (format AAAA-MM-JJ).")
      }
    }

    var trips = TripDAO.filter(departureCityId, arrivalCityId, LocalDate.parse(dateInput))

    if (trips.isEmpty) {
      println("Aucun trajet trouvé pour cette recherche.")
      println("Appuyer sur n'importe quelle touche pour revenir au menu principal.")
      scala.io.StdIn.readLine()
      return
    }

    println(s"Trajets trouves de ${departureCity.get.cityName} a ${arrivalCity.get.cityName} le $dateInput :")
    println("0. Retour au menu principal")
    trips.foreach { trip =>
      println(s"${trip.tripId}." +
        s"\n Conducteur: ${UserDAO.userFindById(trip.tripDriverUserId).get.nom} (${UserDAO.userFindById(trip.tripDriverUserId).get.note} etoiles)" +
        s"\nDate: ${trip.tripDate}" +
        s"\nPrix: ${trip.tripPrice} euros"
      )}

    println("Selectionnez le numero du trajet que vous souhaitez reserver : ")
    continue = true
    var chosenTrip: Trip = null
    while (continue) {
      val chosenTripId = scala.io.StdIn.readInt()
      if (chosenTripId == 0) {
        println("Retour au menu principal.")
        return
      } else if (trips.exists(_.tripId == chosenTripId)) {
        chosenTrip = trips.find(_.tripId == chosenTripId).get
        continue = false
      } else {
        println("Trajet non valide, veuillez reessayer.")
      }
    }

    val driver = UserDAO.userFindById(chosenTrip.tripDriverUserId).get

    println("| Details du trajet :")
    println(s"| ${departureCity.get.cityName} - ${arrivalCity.get.cityName}")
    println(s"| Conducteur: ${driver.nom} (${driver.note} etoiles)")
    println(s"| Depart : $dateInput")
    println(s"| Vehicule : ${driver.vehicule}")
    println(s"| Prix : ${chosenTrip.tripPrice} euros")
    println(s"| ${chosenTrip.tripPassengersSeatsNumber} places restantes")

    // appuyer sur une touche pour continuer la réservation
    println("Appuyez sur une touche pour continuer la réservation")
    scala.io.StdIn.readLine()

    println("| Paiement du trajet")
    println(s"| ${chosenTrip.tripPrice} euros")

    println("Confirmer le paiement ?")
    println("0. Retour au menu principal")
    println("1. Oui")

    continue = true
    while (continue) {
      val choix = scala.io.StdIn.readInt()
      choix match {
        case 0 =>
          continue = false
        case 1 =>
          ReservationDAO.insert(Reservation(
            resId = 0, // Auto-incremented by the database
            tripId = chosenTrip.tripId,
            resPassengerUserId = UserDAO.userFindById(chosenTrip.tripDriverUserId).get.userId,
            resIsCanceled = false,
            resPassengerTripPrice = chosenTrip.tripPrice.floatValue(),
            resDate = java.time.LocalDateTime.now()
          ))
          println("/// Trajet reserve !")
          continue = false
        case _ =>
          println("Commande invalide, veuillez reessayer.")
      }
    }
  }
}