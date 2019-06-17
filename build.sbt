name := "priceScraper"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "net.ruippeixotog" %% "scala-scraper" % "2.1.0"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.5"
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.144-R12"

resourceDirectory in Compile := file("res")