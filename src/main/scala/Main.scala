import scala.io.StdIn

@main
def main(): Unit = {
  var user: user = null
  var continue = true

  while (continue) {
    if (user != null) {
      println("1: Inscription")
      println("2: Connexion")

      println("Selectionnez une action")
      val choix = StdIn.readInt()

      choix match {
        case 1 =>
          println("Inscription")

        case 2 =>
          println("Connexion")
      }
    }
    else {
      val menu = List("Utilisateur", "Reservation", "Messagerie")

      println("Menu principal :")
      for ((section, index) <- menu.zipWithIndex) {
        println(s"${index + 1} : $section")
      }

      println("Selectionnez une action")
      val choix = StdIn.readInt()

      choix match {
        case 1 =>
          println("Utilisateur")

        case 2 =>
          println("Reservation")

        case 3 =>
          println("Messagerie")
      }
    }
  }


}

