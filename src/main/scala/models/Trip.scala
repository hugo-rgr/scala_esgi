package models

import java.time.LocalDateTime

case class Trip(
  tripId: Int,
  tripDepartureCityId: Int,
  tripArrivalCityId: Int,
  tripDate: LocalDateTime,
  tripDriverUserId: Int,
  tripReservationId: Int,
  tripPassengersSeatsNumber: Int
)