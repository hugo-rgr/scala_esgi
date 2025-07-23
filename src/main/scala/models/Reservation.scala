package models

import java.time.LocalDateTime

case class Reservation(
                        resId: Int,
                        tripId: Int,
                        resPassengerUserId: Int,
                        resIsCanceled: Boolean = false,
                        resPassengerTripPrice: Float,
                        resDate: LocalDateTime,
                        resIsRated: Option[Boolean] = Some(false)
                      )