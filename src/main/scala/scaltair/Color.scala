package scaltair

// an enumeration that represents colors as rgb with the standard colors
enum Color(val r: Int, val g: Int, val b: Int):
  case Black extends Color(0, 0, 0)
  case White extends Color(255, 255, 255)
  case Red extends Color(255, 0, 0)
  case Green extends Color(0, 255, 0)
  case Blue extends Color(0, 0, 255)
  case Yellow extends Color(255, 255, 0)
  case Cyan extends Color(0, 255, 255)
  case Magenta extends Color(255, 0, 255)
  case RGB(red: Int, green: Int, blue: Int) extends Color(red, green, blue)
