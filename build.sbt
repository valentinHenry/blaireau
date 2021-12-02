name := "blaireau"

version := "0.0.1"

scalacOptions ++= Seq(
  "-Ymacro-annotations"
)

// Global Settings
lazy val commonSettings = Seq(
  scalafmtOnCompile := true,
  // Resolvers
  resolvers += Resolver.sonatypeRepo("public"),
  resolvers += Resolver.sonatypeRepo("snapshots"),
  // Publishing
  organization := "fr.valentinhenry",
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
  ),
  // Compilation
  scalaVersion := "2.13.7",
  scalacOptions -= "-language:experimental.macros", // doesn't work cross-version
  Compile / doc / scalacOptions --= Seq("-Xfatal-warnings"),
  Compile / doc / scalacOptions ++= Seq(
    "-groups",
    "-sourcepath",
    (LocalRootProject / baseDirectory).value.getAbsolutePath
  ),
  libraryDependencies ++= Seq(
    compilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.2").cross(CrossVersion.full))
  ).filterNot(_ => scalaVersion.value.startsWith("3."))
)

lazy val blaireau = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin)
  .settings(commonSettings)
  .settings(publish / skip := true)
  .dependsOn(dsl, `derivation-codec`)
  .aggregate(dsl, `derivation-codec`)
  .enablePlugins(ScalafmtPlugin)

lazy val `derivation-codec` = project
  .in(file("modules/derivation"))
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(ScalafmtPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.`skunk`,
      Dependencies.`shapeless`,
      Dependencies.`magnolia`,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
    )
  )

lazy val dsl = project
  .in(file("modules/dsl"))
  .enablePlugins(AutomateHeaderPlugin)
  .enablePlugins(ScalafmtPlugin)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.`skunk`,
      Dependencies.`shapeless`,
      Dependencies.`magnolia`,
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % Provided
    )
  )
