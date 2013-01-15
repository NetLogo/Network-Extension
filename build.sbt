scalaVersion := "2.10.0"

scalaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-Xfatal-warnings",
                      "-encoding", "us-ascii")

libraryDependencies +=
  "org.nlogo" % "NetLogoHeadless" % "5.x-8568f6f4" from
    "http://ccl.northwestern.edu/devel/NetLogoHeadless-8568f6f4.jar"

artifactName := { (_, _, _) => "network.jar" }

packageOptions := Seq(
  Package.ManifestAttributes(
    ("Extension-Name", "network"),
    ("Class-Manager", "org.nlogo.extensions.network.NetworkExtension"),
    ("NetLogo-Extension-API-Version", "5.0")))

packageBin in Compile <<= (packageBin in Compile, baseDirectory, streams) map {
  (jar, base, s) =>
    IO.copyFile(jar, base / "network.jar")
    Process("pack200 --modification-time=latest --effort=9 --strip-debug " +
            "--no-keep-file-order --unknown-attribute=strip " +
            "network.jar.pack.gz network.jar").!!
    if(Process("git diff --quiet --exit-code HEAD").! == 0) {
      Process("git archive -o network.zip --prefix=network/ HEAD").!!
      IO.createDirectory(base / "network")
      IO.copyFile(base / "network.jar", base / "network" / "network.jar")
      IO.copyFile(base / "network.jar.pack.gz", base / "network" / "network.jar.pack.gz")
      Process("zip network.zip network/network.jar network/network.jar.pack.gz").!!
      IO.delete(base / "network")
    }
    else {
      s.log.warn("working tree not clean; no zip archive made")
      IO.delete(base / "network.zip")
    }
    jar
  }

cleanFiles <++= baseDirectory { base =>
  Seq(base / "network.jar",
      base / "network.jar.pack.gz",
      base / "network.zip") }
