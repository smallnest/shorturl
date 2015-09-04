import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import sbt.Defaults._
import sbt.Keys._
import sbt._
import sbtassembly.MergeStrategy
import sbtrelease.ReleasePlugin._

object Build extends sbt.Build {
  val netty_version = "5.0.0.Alpha2"
  val casbah_version = "2.8.2"
  val salat_version = "1.9.9"
  val shade_version = "1.6.0"

  val typesafe_config_version = "1.3.0"
  val scala_logging_version = "3.1.0"
  val logback_version = "1.1.3"
  val scalaTestVersion = "2.2.4"

  val commondependencies = Seq(
    "com.typesafe" % "config" % typesafe_config_version,
    "com.typesafe.scala-logging" %% "scala-logging" % scala_logging_version,
    "ch.qos.logback" % "logback-classic" % logback_version
  )

  val testDeps = Seq(
    "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
  )

  val shortURLdependencies = Seq(
    "io.netty" % "netty-all" % netty_version,
    "io.netty" % "netty-transport-native-epoll" % netty_version classifier "linux-x86_64",
    "org.mongodb" %% "casbah" % casbah_version,
    "com.novus" %% "salat" % salat_version,
    "com.bionicspirit" %% "shade" % shade_version
  ) ++ commondependencies ++ testDeps



  lazy val root = Project("uridb", file("."))
    .settings(defaultSettings: _*)
    .aggregate(shortURL)


  lazy val shortURL = Project("shortURL", file("shortURL"))
    .settings(defaultSettings: _*)
    .enablePlugins(JavaAppPackaging)
    .settings(libraryDependencies ++= shortURLdependencies)
    .settings(mainClass in Compile := Some("com.uridb.shorturl.ShortURLServer"))

  lazy val defaultSettings = coreDefaultSettings ++ releaseSettings ++ Seq(
    organization := "com.uridb",
    version := "1.0",
    externalResolvers := Resolvers.all,
    scalaVersion := "2.11.7",
    scalacOptions := Seq("-deprecation", "-feature")

  )

  object Resolvers {
    val jgitrepo = "jgit-repo" at "http://download.eclipse.org/jgit/maven"
    val typesafe = "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
    val sbtPlugins = "sbt-plugins" at "http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases/"
    val mvnrepository = "mvnrepository" at "http://mvnrepository.com/artifact/"
    val mavenCenter = "Maven Central Server" at "http://repo1.maven.org/maven2/"
    val spy = "Spy" at "http://files.couchbase.com/maven2/"
    val all = Seq(mvnrepository, mavenCenter, jgitrepo, typesafe, sbtPlugins, spy)
  }

}