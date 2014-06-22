import android.Keys._

import android.Dependencies.aar

android.Plugin.androidBuild


name := "gyst" // CHANGE THIS

platformTarget in Android := "android-19"

scalaVersion := "2.11.1"
 
resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "jcenter" at "http://jcenter.bintray.com"
)

scalacOptions in (Compile, compile) ++= Seq(
  "-P:wartremover:cp:" + (dependencyClasspath in Compile).value
    .files.map(_.toURL.toString)
    .find(_.contains("org.macroid/macroid_")).get,
  "-P:wartremover:traverser:macroid.warts.CheckUi"
)

libraryDependencies ++= Seq(
  "org.macroid" %% "macroid" % "2.0.0-M1"
  , aar("net.danlew" % "android.joda" % "2.3.3")
  , aar("com.doomonafireball.betterpickers" % "library" % "1.5.2")
  , compilerPlugin("org.brianmckenna" %% "wartremover" % "0.10")
)

proguardOptions in Android ++= Seq(
  "-keep public class * extends junit.framework.TestCase",
  "-keepclassmembers class * extends junit.framework.TestCase { *; }"
)
 
run <<= run in Android
 
install <<= install in Android
