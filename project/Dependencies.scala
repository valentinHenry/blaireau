import sbt._

object Dependencies {
  val `magnolia`  = "com.propensive" %% "magnolia"   % Versions.magnolia
  val `skunk`     = "org.tpolecat"   %% "skunk-core" % Versions.skunk
  val `shapeless` = "com.chuusai"    %% "shapeless"  % Versions.shapeless

  val `munit` = "org.scalameta" %% "munit" % Versions.munit % Test
}
