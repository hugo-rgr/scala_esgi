package models

case class User(
  val userId: Int = 0 ,
  val nom : String,
  val vehicule : String = null,
  val note : Double = 0.0,
  val nombreNote : Int = 0
)