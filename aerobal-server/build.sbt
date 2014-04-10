name := "aerobal-server"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  javaJpa,
  "org.postgresql" % "postgresql" % "9.3-1101-jdbc4",
  "org.hibernate" % "hibernate-core" % "4.3.5.Final",
  "com.google.code.gson"%"gson"%"2.2.4",
  "org.dbunit" % "dbunit" % "2.4.9",
  "org.hsqldb" % "hsqldb" % "2.3.2",
  "junit" % "junit" % "3.8.2"
)     

play.Project.playScalaSettings
