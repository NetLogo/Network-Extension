scalaVersion := "2.10.0"

scalaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-Xfatal-warnings",
                      "-encoding", "us-ascii")

libraryDependencies +=
  "org.nlogo" % "NetLogoHeadless" % "5.x-e4dbb79c" from
    "http://ccl.northwestern.edu/devel/NetLogoHeadless-e4dbb79c.jar"

name := "network"

NetLogoExtension.settings

NetLogoExtension.classManager := "org.nlogo.extensions.network.NetworkExtension"
