name := "aerobal-server"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  javaJpa,
  "org.postgresql" % "postgresql" % "9.3-1101-jdbc4",
  "org.hibernate" % "hibernate-entitymanager" % "4.3.5.Final",
  "com.google.code.gson"%"gson"%"2.2.4"
)     

play.Project.playScalaSettings
