import sbt._

object Dependencies {
  val `skunk`       = "org.tpolecat" %% "skunk-core"  % Versions.skunk
  val `skunk-circe` = "org.tpolecat" %% "skunk-circe" % Versions.skunk
  val `newtype`     = "io.estatico"  %% "newtype"     % Versions.newtype
  val `shapeless`   = "com.chuusai"  %% "shapeless"   % Versions.shapeless
  val `refined`     = "eu.timepit"   %% "refined"     % Versions.refined

  val `munit` = "org.scalameta" %% "munit" % Versions.munit % Test
}
