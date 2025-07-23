package dao

import Utils.DBConnection
import models.City

import java.sql.PreparedStatement
import java.sql.*

object CityDAO {
  def createTable(): Unit = {
    val requete =
      """CREATE TABLE City (
        |city_id INT PRIMARY KEY AUTO_INCREMENT,
        |city_name VARCHAR(50) NOT NULL
        |);
        |""".stripMargin

    val statement = DBConnection.connection.prepareStatement(requete)
    statement.execute()
  }
  
  def findAll(): List[City] = {
    val requete = "SELECT * FROM City"
    val statement = DBConnection.connection.prepareStatement(requete)
    val result = statement.executeQuery()
    var cities = List[City]()

    while (result.next()) {
      val city = City(
        cityId = result.getInt("city_id"),
        cityName = result.getString("city_name"),
      )
      cities = city :: cities
    }
    cities.reverse
  }

  def find(id: Int): Option[City] = {
    val requete = "SELECT * FROM City WHERE city_id = ?"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setInt(1, id)
    val result = statement.executeQuery()
    Option.when(result.next()) {
      City(cityId = result.getInt("city_id"), cityName = result.getString("city_name"))
    }
  }

  def insert(city: City): Unit = {
    val requete = "INSERT INTO City (city_name) VALUES (?)"
    val statement = DBConnection.connection.prepareStatement(requete)
    statement.setString(1, city.cityName)
    statement.executeUpdate()
  }
}
