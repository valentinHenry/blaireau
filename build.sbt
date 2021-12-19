// Global Settings
lazy val commonSettings = Seq(
  // Compilation
  version      := "0.0.1",
  scalaVersion := "2.13.7",
  scalacOptions ++= Seq("-Ymacro-annotations"),
  organization := "fr.valentinhenry",
  // ScalaFmt
  scalafmtOnCompile := true,
  // Resolvers
  resolvers += Resolver.sonatypeRepo("public"),
  resolvers += Resolver.sonatypeRepo("snapshots"),
  // Publishing
  licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  developers := List(
    Developer("Firiath", "Valentin Henry", "valentin.hnry@gmail.com", url("https://valentin-henry.fr"))
  ),
  // Headers
  headerMappings := headerMappings.value + (HeaderFileType.scala -> HeaderCommentStyle.cppStyleLineComment),
  headerLicense := Some(
    HeaderLicense.Custom(
      """|Written by Valentin HENRY
         |
         |This software is licensed under the MIT License (MIT).
         |For more information see LICENSE or https://opensource.org/licenses/MIT
         |""".stripMargin
    )
  )
)

lazy val blaireau = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(publish / skip := true)
  .dependsOn(`codec-derivation`, dsl, tests)
  .aggregate(`codec-derivation`, dsl, tests)
  .enablePlugins(ScalafmtPlugin)

lazy val `codec-derivation` = project
  .in(file("modules/derivation"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.`skunk`,
      Dependencies.`shapeless`
    )
  )
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(ScalafmtPlugin)

lazy val dsl = project
  .in(file("modules/dsl"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.`skunk`,
      Dependencies.`shapeless`
    )
  )
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(ScalafmtPlugin)

lazy val tests = project
  .in(file("modules/tests"))
  .settings(commonSettings)
  .settings(
    publish / skip := true,
    libraryDependencies += Dependencies.`munit`
  )
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(ScalafmtPlugin)
  .dependsOn(`codec-derivation`, dsl)
