package priceScraper

object MainAppl {
  def main(args: Array[String]): Unit = {
    if (args.length > 0 && args(0) == "scrap")
      ScraperAppl.main(args)
    else
      ScalaFXAppl.main(args)
  }
}
