package dao

import Utils.DBConnection
import models.Reservation

object ReservationDAO {

  def find(id: Int): Option[Reservation] = {
    val requete = "SELECT * FROM Reservation WHERE res_id = ? AND res_is_canceled = 0"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, id)
    val result = statement.executeQuery()
    if (result.next()) {
      Some(Reservation(
        resId = result.getInt("res_id"),
        tripId = result.getInt("trip_id"),
        resPassengerUserId = result.getInt("res_passenger_user_id"),
        resIsCanceled = result.getBoolean("res_is_canceled"),
        resPassengerTripPrice = result.getFloat("res_passenger_trip_price"),
        resDate = result.getTimestamp("res_date").toLocalDateTime,
        resIsRated = Option(result.getBoolean("res_is_rated")).filter(_ => !result.wasNull())
      ))
    } else {
      None
    }
  }

  def findAll(): List[Reservation] = {
    val requete = "SELECT * FROM Reservation WHERE res_is_canceled = 0"
    val statement = DBConnection.connection.createStatement()
    val result = statement.executeQuery(requete)
    var reservations = List[Reservation]()
    while (result.next()) {
      val reservation = Reservation(
        resId = result.getInt("res_id"),
        tripId = result.getInt("trip_id"),
        resPassengerUserId = result.getInt("res_passenger_user_id"),
        resIsCanceled = result.getBoolean("res_is_canceled"),
        resPassengerTripPrice = result.getFloat("res_passenger_trip_price"),
        resDate = result.getTimestamp("res_date").toLocalDateTime,
        resIsRated = Option(result.getBoolean("res_is_rated")).filter(_ => !result.wasNull())
      )
      reservations = reservation :: reservations
    }
    reservations.reverse
  }

  def insert(reservation: Reservation): Reservation = {
    val requete = "INSERT INTO Reservation (trip_id, res_passenger_user_id, res_is_canceled, res_passenger_trip_price, res_date, res_is_rated) VALUES (?, ?, ?, ?, ?, ?)"
    val statement = DBConnection.connection.prepareStatement(requete, java.sql.Statement.RETURN_GENERATED_KEYS)
    statement.setInt(1, reservation.tripId)
    statement.setInt(2, reservation.resPassengerUserId)
    statement.setBoolean(3, reservation.resIsCanceled)
    statement.setFloat(4, reservation.resPassengerTripPrice)
    statement.setTimestamp(5, java.sql.Timestamp.valueOf(reservation.resDate))

    reservation.resIsRated match {
      case Some(value) => statement.setBoolean(6, value)
      case None => statement.setNull(6, java.sql.Types.BIT)
    }

    statement.executeUpdate()

    val generatedKeys = statement.getGeneratedKeys
    if (generatedKeys.next()) {
      reservation.copy(resId = generatedKeys.getInt(1))
    } else {
      throw new Exception("Failed to insert reservation and retrieve generated ID.")
    }
  }

  def update(reservation: Reservation): Unit = {
    val requete = "UPDATE Reservation SET trip_id = ?, res_passenger_user_id = ?, res_is_canceled = ?, res_passenger_trip_price = ?, res_date = ?, res_is_rated = ? WHERE res_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, reservation.tripId)
    statement.setInt(2, reservation.resPassengerUserId)
    statement.setBoolean(3, reservation.resIsCanceled)
    statement.setFloat(4, reservation.resPassengerTripPrice)
    statement.setTimestamp(5, java.sql.Timestamp.valueOf(reservation.resDate))

    reservation.resIsRated match {
      case Some(value) => statement.setBoolean(6, value)
      case None => statement.setNull(6, java.sql.Types.BIT)
    }

    statement.setInt(7, reservation.resId)
    statement.executeUpdate()
  }

  def updateIsRated(resId: Int, isRated: Option[Boolean]): Unit = {
    val requete = "UPDATE Reservation SET res_is_rated = ? WHERE res_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)

    isRated match {
      case Some(value) => statement.setBoolean(1, value)
      case None => statement.setNull(1, java.sql.Types.BIT)
    }

    statement.setInt(2, resId)
    statement.executeUpdate()
  }

  def updateIsCancelled(resId: Int, isCanceled: Option[Boolean]): Unit = {
    val requete = "UPDATE Reservation SET res_is_canceled = ? WHERE res_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)

    isCanceled match {
      case Some(value) => statement.setBoolean(1, value)
      case None => statement.setNull(1, java.sql.Types.BIT)
    }

    statement.setInt(2, resId)
    statement.executeUpdate()
  }

  def delete(id: Int): Unit = {
    val requete = "DELETE FROM Reservation WHERE res_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, id)
    statement.executeUpdate()
  }

  // Additional method to find reservations by passenger user ID
  def findByPassengerUserId(passengerUserId: Int): List[Reservation] = {
    val requete = "SELECT * FROM Reservation WHERE res_passenger_user_id = ? AND res_is_canceled = 0"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, passengerUserId)
    val result = statement.executeQuery()
    var reservations = List[Reservation]()
    while (result.next()) {
      val reservation = Reservation(
        resId = result.getInt("res_id"),
        tripId = result.getInt("trip_id"),
        resPassengerUserId = result.getInt("res_passenger_user_id"),
        resIsCanceled = result.getBoolean("res_is_canceled"),
        resPassengerTripPrice = result.getFloat("res_passenger_trip_price"),
        resDate = result.getTimestamp("res_date").toLocalDateTime,
        resIsRated = Option(result.getBoolean("res_is_rated")).filter(_ => !result.wasNull())
      )
      reservations = reservation :: reservations
    }
    reservations.reverse
  }

  // Additional method to find reservations by trip ID
  def findByTripId(tripId: Int): List[Reservation] = {
    val requete = "SELECT * FROM Reservation WHERE trip_id = ? AND res_is_canceled = 0"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, tripId)
    val result = statement.executeQuery()
    var reservations = List[Reservation]()
    while (result.next()) {
      val reservation = Reservation(
        resId = result.getInt("res_id"),
        tripId = result.getInt("trip_id"),
        resPassengerUserId = result.getInt("res_passenger_user_id"),
        resIsCanceled = result.getBoolean("res_is_canceled"),
        resPassengerTripPrice = result.getFloat("res_passenger_trip_price"),
        resDate = result.getTimestamp("res_date").toLocalDateTime,
        resIsRated = Option(result.getBoolean("res_is_rated")).filter(_ => !result.wasNull())
      )
      reservations = reservation :: reservations
    }
    reservations.reverse
  }
}