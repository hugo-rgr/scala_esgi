package dao

import Utils.DBConnection
import models.Trip

import java.time.LocalDate

object TripDAO {
  def find(id: Int): Option[Trip] = {
    val requete = "SELECT * FROM Trip WHERE trip_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, id)
    val result = statement.executeQuery()

    if (result.next()) {
      Some(Trip(
        tripId = result.getInt("trip_id"),
        tripDepartureCityId = result.getInt("trip_departure_city_id"),
        tripArrivalCityId = result.getInt("trip_arrival_city_id"),
        tripDate = result.getTimestamp("trip_date").toLocalDateTime,
        tripDriverUserId = result.getInt("trip_driver_user_id"),
        tripPassengersSeatsNumber = result.getInt("trip_passengers_seats_number"),
        tripPrice = result.getBigDecimal("trip_price")
      ))
    } else {
      None
    }
  }

  def filter(departureCityId: Int, arrivalCityId: Int, date: LocalDate): List[Trip] = {
    val requete = "SELECT * FROM Trip WHERE trip_departure_city_id = ? " +
      "AND trip_arrival_city_id = ? " +
      "AND DATE(trip_date) = ? " +
      "AND trip_passengers_seats_number > 0"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, departureCityId)
    statement.setInt(2, arrivalCityId)
    statement.setDate(3, java.sql.Date.valueOf(date))

    val result = statement.executeQuery()
    var trips = List[Trip]()

    while (result.next()) {
      val trip = Trip(
        tripId = result.getInt("trip_id"),
        tripDepartureCityId = result.getInt("trip_departure_city_id"),
        tripArrivalCityId = result.getInt("trip_arrival_city_id"),
        tripDate = result.getTimestamp("trip_date").toLocalDateTime,
        tripDriverUserId = result.getInt("trip_driver_user_id"),
        tripPassengersSeatsNumber = result.getInt("trip_passengers_seats_number"),
        tripPrice = result.getBigDecimal("trip_price")
      )
      trips = trip :: trips
    }
    trips.reverse
  }

  def findAll(): List[Trip] = {
    val requete = "SELECT * FROM Trip"
    val statement = DBConnection.connection.createStatement()
    val result = statement.executeQuery(requete)
    var trips = List[Trip]()

    while (result.next()) {
      val trip = Trip(
        tripId = result.getInt("trip_id"),
        tripDepartureCityId = result.getInt("trip_departure_city_id"),
        tripArrivalCityId = result.getInt("trip_arrival_city_id"),
        tripDate = result.getTimestamp("trip_date").toLocalDateTime,
        tripDriverUserId = result.getInt("trip_driver_user_id"),
        tripPassengersSeatsNumber = result.getInt("trip_passengers_seats_number"),
        tripPrice = result.getBigDecimal("trip_price")
      )
      trips = trip :: trips
    }
    trips.reverse
  }

  def insert(trip: Trip): Unit = {
    val requete = "INSERT INTO Trip (trip_departure_city_id, trip_arrival_city_id, trip_date, trip_driver_user_id, trip_passengers_seats_number, trip_price) VALUES (?, ?, ?, ?, ?, ?)"
    val statement = DBConnection.connection.prepareStatement(requete, java.sql.Statement.RETURN_GENERATED_KEYS)
    statement.setInt(1, trip.tripDepartureCityId)
    statement.setInt(2, trip.tripArrivalCityId)
    statement.setTimestamp(3, java.sql.Timestamp.valueOf(trip.tripDate))
    statement.setInt(4, trip.tripDriverUserId)
    statement.setInt(5, trip.tripPassengersSeatsNumber)
    statement.setBigDecimal(6, trip.tripPrice.bigDecimal)

    statement.executeUpdate()

    val generatedKeys = statement.getGeneratedKeys
    if (generatedKeys.next()) {
      trip.copy(tripId = generatedKeys.getInt(1))
    } else {
      throw new Exception("Failed to insert trip and retrieve generated ID.")
    }
  }

  def update(trip: Trip): Unit = {
    val requete = "UPDATE Trip SET " +
      "trip_departure_city_id = ?, trip_arrival_city_id = ?, trip_date = ?, trip_driver_user_id = ?, " +
      "trip_passengers_seats_number = ?, trip_price = ? WHERE trip_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, trip.tripDepartureCityId)
    statement.setInt(2, trip.tripArrivalCityId)
    statement.setTimestamp(3, java.sql.Timestamp.valueOf(trip.tripDate))
    statement.setInt(4, trip.tripDriverUserId)
    statement.setInt(5, trip.tripPassengersSeatsNumber)
    statement.setBigDecimal(6, trip.tripPrice.bigDecimal)
    statement.setInt(7, trip.tripId)

    statement.executeUpdate()
  }

  def delete(id: Int): Unit = {
    val requete = "DELETE FROM Trip WHERE trip_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, id)
    statement.executeUpdate()
  }
}