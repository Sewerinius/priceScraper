import net.ruippeixotog.scalascraper.model.{Element, ElementQuery}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, JsValue, RootJsonFormat}

//trait AttributeExtractor extends HtmlExtractor[Element, String] {
//  def extract(doc: ElementQuery[Element]): String
//}
//
//case class TextAttributeExtractor(selector: String) extends AttributeExtractor {
//  override def extract(doc: ElementQuery[Element]): String = {
//    doc >> text(selector)
//  }
//}
//
//case class PropertyAttributeExtractor(selector: String, property: String) extends AttributeExtractor {
//  override def extract(doc: ElementQuery[Element]): String = {
//    doc >> attr(property)(selector)
//  }
//}
trait AttributeExtractor {
  def extract(doc: Element): String
}

case class TextAttributeExtractor(selector: String) extends AttributeExtractor {
  override def extract(doc: Element): String = {
    doc >> text(selector)
  }
}

case class PropertyAttributeExtractor(selector: String, property: String) extends AttributeExtractor {
  override def extract(doc: Element): String = {
    doc >> attr(property)(selector)
  }
}

object AttributeExtractor {
  implicit val textAttributeExtractorFormat: RootJsonFormat[TextAttributeExtractor] = jsonFormat1(TextAttributeExtractor)
  implicit val propertyAttributeExtractorFormat: RootJsonFormat[PropertyAttributeExtractor] = jsonFormat2(PropertyAttributeExtractor)

  implicit object AttributeExtractorJsonFormat extends RootJsonFormat[AttributeExtractor] {
    override def write(obj: AttributeExtractor): JsValue = obj match {
      case x: TextAttributeExtractor => textAttributeExtractorFormat.write(x)
      case x: PropertyAttributeExtractor => propertyAttributeExtractorFormat.write(x)
    }

    @throws(classOf[IllegalArgumentException])
    override def read(json: JsValue): AttributeExtractor = {
        json.asJsObject.fields("type") match {
          case JsString("text") => json.convertTo[TextAttributeExtractor]
          case JsString("prop") => json.convertTo[PropertyAttributeExtractor]
          case _ => throw new IllegalArgumentException
      }
    }
  }

}
