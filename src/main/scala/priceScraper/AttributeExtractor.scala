package priceScraper

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.{Element, ElementQuery}
import net.ruippeixotog.scalascraper.scraper.HtmlExtractor
import spray.json.DefaultJsonProtocol._
import spray.json.{JsString, JsValue, RootJsonFormat}

import scala.util.matching.Regex

trait AttributeExtractor extends HtmlExtractor[Element, String]

case class TextAttributeExtractor(selector: String, regex: String) extends priceScraper.AttributeExtractor {
  override def extract(q: ElementQuery[Element]): String = {
    regex.r.findFirstMatchIn(q >> text(selector)).get.subgroups.mkString
  }
}

case class PropertyAttributeExtractor(selector: String, property: String) extends AttributeExtractor {
  override def extract(q: ElementQuery[Element]): String = {
    q >> attr(property)(selector)
  }
}

//trait AttributeExtractor {
//  def extract(doc: Element): String
//  val extractor: HtmlExtractor[Element, String]
//}
//
//case class TextAttributeExtractor(selector: String) extends AttributeExtractor {
//  override def extract(doc: Element): String = {
//    doc >> this.extractor
//  }
//  override val extractor: HtmlExtractor[Element, String] = text(selector)
//}
//
//case class PropertyAttributeExtractor(selector: String, property: String) extends AttributeExtractor {
//  override def extract(doc: Element): String = {
//    doc >> this.extractor
//  }
//  override val extractor: HtmlExtractor[Element, String] = attr(property)(selector)
//}

object AttributeExtractor {
  implicit val textAttributeExtractorFormat: RootJsonFormat[TextAttributeExtractor] = jsonFormat2(TextAttributeExtractor)
  implicit val propertyAttributeExtractorFormat: RootJsonFormat[PropertyAttributeExtractor] = jsonFormat2(PropertyAttributeExtractor)
//  implicit val textAttributeExtractorFormat: RootJsonFormat[TextAttributeExtractor] = jsonFormat[String, TextAttributeExtractor](TextAttributeExtractor, "selector")
//  implicit val propertyAttributeExtractorFormat: RootJsonFormat[PropertyAttributeExtractor] = jsonFormat[String, String, PropertyAttributeExtractor](PropertyAttributeExtractor, "selector", "property")

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


//  implicit def AttributeExtractor2HtmlExtractor(obj: AttributeExtractor): HtmlExtractor[Element, String] = obj.extractor
}
