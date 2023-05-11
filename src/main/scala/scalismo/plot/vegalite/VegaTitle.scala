package scalismo.plot.vegalite

import scalismo.plot.json.JsonObject
import scalismo.plot.json.JsonString
import scalismo.plot.json.JsonArray
import scalismo.plot.json.JsonValue
import scalismo.plot.json.JsonNumber

case class VegaTitle(text: String, props: Seq[TitleProp] = Seq.empty)
    extends VegaLite:
  override def spec: JsonObject =
    JsonObject(
      Seq("text" -> JsonString(text)) ++ props.map(prop =>
        prop.name -> prop.spec
      )
    )

enum TitleProp(val name: String, val spec: JsonValue):
  case FontSize(size: Int) extends TitleProp("fontSize", JsonNumber(size))
