scalaVersion := "2.9.2"

scalaSource in Compile := baseDirectory.value / "src"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-Xfatal-warnings", "-encoding", "us-ascii")

libraryDependencies +=
  "org.nlogo" % "NetLogo" % "5.3.0" from
    "http://ccl-artifacts.s3-website-us-east-1.amazonaws.com/NetLogo-5.3.0.jar"

name := "network"

enablePlugins(org.nlogo.build.NetLogoExtension)

netLogoExtName := "network"

netLogoClassManager := "org.nlogo.extensions.network.NetworkExtension"

netLogoZipSources := false

netLogoTarget :=
    org.nlogo.build.NetLogoExtension.directoryTarget(baseDirectory.value)
