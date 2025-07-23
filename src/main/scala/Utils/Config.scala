package Utils
import io.github.cdimascio.dotenv.Dotenv

object Config {
  private val dotenv = Dotenv.load()

  val dbUrl: String = dotenv.get("DB_URL")
  val dbUser: String = dotenv.get("DB_USER")
  val dbPass: String = dotenv.get("DB_PASS")
}
