import sbt._
import Keys._
import PlayProject._
import sbtbuildinfo.Plugin._

object ApplicationBuild extends Build {

    val appName         = "org.qibud.server"
    val appVersion      = "1.0-SNAPSHOT"

    val qiBudApi = Project(
       "org.qibud.api", file( "modules/qibud-api" )
    )

    val appDependencies = Seq(
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA,
      settings = Defaults.defaultSettings ++ buildInfoSettings
    ).dependsOn(
      qiBudApi
    ).settings(
      // Add your own project settings here      
      sourceGenerators in Compile <+= buildInfo,
      buildInfoKeys := Seq[Scoped](name, version, scalaVersion, sbtVersion),
      buildInfoPackage := "utils"
    )

}
