// Global Settings
lazy val commonSettings = Seq(
  // Compilation
  scalaVersion := "2.13.7",
  scalacOptions ++= Seq("-Ymacro-annotations", "-language:dynamics"),
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
      """|Written by Valentin "Firiath" Henry
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
  .dependsOn(dsl, refined, newtype, tests)
  .aggregate(dsl, refined, newtype, tests)
  .enablePlugins(ScalafmtPlugin)
  .enablePlugins(AutomateHeaderPlugin)

lazy val dsl = project
  .in(file("modules/dsl"))
  .settings(
    name        := "blaireau-dsl",
    description := "A postgresql DSL for Skunk."
  )
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.`skunk`,
      Dependencies.`shapeless`
    )
  )
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(ScalafmtPlugin)

lazy val newtype = project
  .in(file("modules/newtype"))
  .settings(
    name        := "blaireau-newtype",
    description := "Metas for estatico newtypes."
  )
  .settings(commonSettings)
  .settings(libraryDependencies += Dependencies.`newtype`)
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(ScalafmtPlugin)
  .dependsOn(dsl)

lazy val refined = project
  .in(file("modules/refined"))
  .settings(
    name        := "blaireau-refined",
    description := "Metas for timepit refined."
  )
  .settings(commonSettings)
  .settings(libraryDependencies += Dependencies.`refined`)
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(ScalafmtPlugin)
  .dependsOn(dsl)

lazy val tests = project
  .in(file("modules/tests"))
  .settings(
    name           := "blaireau-tests",
    description    := "Test suite of Blaireau.",
    publish / skip := true
  )
  .settings(commonSettings)
  .settings(libraryDependencies += Dependencies.`munit`)
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(ScalafmtPlugin)
  .dependsOn(dsl, refined, newtype)
