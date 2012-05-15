import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "org.qibud.server"
    val appVersion      = "1.0-SNAPSHOT"

    val qiBudApi = Project(
       "org.qibud.api", file( "modules/qibud-api" )
    )

    val appDependencies = Seq(
      // Add your project dependencies here,
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA
    ).dependsOn(
      qiBudApi
    ).settings(
      // Add your own project settings here      
    )

}
