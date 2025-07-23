
@main
def main(): Unit = {
  var user : user = null
  var continue = true

  while(continue){
    if(user != null) {
      println("inscription")
      println("connexion")
    }
    else
    {
      val menu = List("Utilisateur", "Reservation", "Messagerie")

      println("Menu principal :")
      for ((section, index) <- menu.zipWithIndex) {
        println(s"${index + 1} : $section")
      }
      
      switch case

      }
  }


  }

