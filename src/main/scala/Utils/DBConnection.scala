package Utils

import java.sql.DriverManager

object DBConnection {
  val url =  Config.dbUrl
  val username = Config.dbUser
  val password = Config.dbPass
  var connection: java.sql.Connection = null
  try {
    connection = DriverManager.getConnection(url, username, password)
    println("Connexion reussie")
  } catch {
    case e: Exception => e.printStackTrace()
  }
}
