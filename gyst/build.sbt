import android.Keys._

import android.Dependencies.aar

android.Plugin.androidBuild


name := "gyst"

platformTarget in Android := "android-19"

scalaVersion := "2.11.1"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "jcenter" at "http://jcenter.bintray.com"
)

scalacOptions in (Compile, compile) ++= Seq(
)

libraryDependencies ++= Seq(
  "com.google.apis" % "google-api-services-calendar" % "v3-rev77-1.18.0-rc" exclude ("org.apache.httpcomponents", "httpclient")
  , "com.google.api-client" % "google-api-client-android" % "1.18.0-rc" exclude ("org.apache.httpcomponents", "httpclient")
  , "com.google.http-client" % "google-http-client-gson" % "1.18.0-rc" exclude ("org.apache.httpcomponents", "httpclient")
  , "com.google.android.gms" % "play-services" % "4.4.52"
  , aar("net.danlew" % "android.joda" % "2.3.3")
  , "io.spray" % "spray-json_2.11.0-RC4" % "1.2.6"
)

proguardOptions in Android ++= Seq(
  "-keep public class * extends junit.framework.TestCase",
  "-keepclassmembers class * extends junit.framework.TestCase { *; }",
  "-dontwarn retrofit.client.**",
  "-dontwarn com.google.appengine.**"
)

apkbuildExcludes in Android ++= Seq(
  "META-INF/LICENSE.txt",
  "META-INF/NOTICE.txt",
  "META-INF/notice.txt",
  "META-INF/license.txt"
)

run <<= run in Android

test <<= test in Android

install <<= install in Android
