package scaltair

enum MarkType:
  case Line
  case Circle
  case Rect
  case Point
  case Bar
  case Area
  case Boxplot
  case ErrorBand

trait View

case class SingleView(mark: MarkType, channels: Seq[Channel]) extends View

case class LayeredView(views: Seq[SingleView]) extends View
