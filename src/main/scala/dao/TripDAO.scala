package dao

import Utils.DBConnection
import models.Trip

object TripDAO {
  def find(id: Int): Option[Trip] = {
    val requete = "SELECT * FROM trips WHERE id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, id)
    val result = statement.executeQuery()

    if (result.next()) {
      Some(Trip(
        tripId = result.getInt("id"),
        tripDepartureCityId = result.getInt("departure_city_id"),
        tripArrivalCityId = result.getInt("arrival_city_id"),
        tripDate = result.getTimestamp("date_depart").toLocalDateTime,
        tripDriverUserId = result.getInt("driver_user_id"),
        tripReservationId = result.getInt("reservation_id"),
        tripPassengersSeatsNumber = result.getInt("passengers_seats_number"),
        tripPrice = result.getBigDecimal("price")
      ))
    } else {
      None
    }
  }

  def findAll(): List[Trip] = {
    val requete = "SELECT * FROM trips"
    val statement = DBConnection.connection.createStatement()
    val result = statement.executeQuery(requete)
    var trips = List[Trip]()

    while (result.next()) {
      val trip = Trip(
        tripId = result.getInt("id"),
        tripDepartureCityId = result.getInt("departure_city_id"),
        tripArrivalCityId = result.getInt("arrival_city_id"),
        tripDate = result.getTimestamp("date_depart").toLocalDateTime,
        tripDriverUserId = result.getInt("driver_user_id"),
        tripReservationId = result.getInt("reservation_id"),
        tripPassengersSeatsNumber = result.getInt("passengers_seats_number"),
        tripPrice = result.getBigDecimal("price")
      )
      trips = trip :: trips
    }
    trips.reverse
  }

  def insert(trip: Trip): Unit = {
    val requete = "INSERT INTO trips (departure_city_id, arrival_city_id, date_depart, driver_user_id, reservation_id, passengers_seats_number, price) VALUES (?, ?, ?, ?, ?, ?, ?)"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, trip.tripDepartureCityId)
    statement.setInt(2, trip.tripArrivalCityId)
    statement.setTimestamp(3, java.sql.Timestamp.valueOf(trip.tripDate))
    statement.setInt(4, trip.tripDriverUserId)
    statement.setInt(5, trip.tripReservationId)
    statement.setInt(6, trip.tripPassengersSeatsNumber)
    statement.setBigDecimal(7, trip.tripPrice.bigDecimal)
    
    statement.executeUpdate()
    
    val generatedKeys = statement.getGeneratedKeys
    if (generatedKeys.next()) {
      trip.copy(tripId = generatedKeys.getInt(1))
    } else {
      throw new Exception("Failed to insert trip and retrieve generated ID.")
    }
  }

  def update(trip: Trip): Unit = {
    val requete = "UPDATE trips SET departure_city_id = ?, arrival_city_id = ?, date_depart = ?, driver_user_id = ?, reservation_id = ?, passengers_seats_number = ?, price = ? WHERE id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, trip.tripDepartureCityId)
    statement.setInt(2, trip.tripArrivalCityId)
    statement.setTimestamp(3, java.sql.Timestamp.valueOf(trip.tripDate))
    statement.setInt(4, trip.tripDriverUserId)
    statement.setInt(5, trip.tripReservationId)
    statement.setInt(6, trip.tripPassengersSeatsNumber)
    statement.setBigDecimal(7, trip.tripPrice.bigDecimal)
    statement.setInt(8, trip.tripId)

    statement.executeUpdate()
  }
  
  
  def delete(id: Int): Unit = {
    val requete = "DELETE FROM trips WHERE id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, id)
    statement.executeUpdate()
  }
}
