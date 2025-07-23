import java.sql.DriverManager

object DBConnection {
  val url = "jdbc:mysql://127.0.0.1:3306/scala_db"
  val username = "root"
  val password = "mysql"
  var connection: java.sql.Connection = null
  try {
    connection = DriverManager.getConnection(url, username, password)
    println("Connexion reussie")
  } catch {
    case e: Exception => e.printStackTrace()
  }
}
