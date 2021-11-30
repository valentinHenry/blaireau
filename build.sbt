name := "blaireau"

version := "0.1"

scalaVersion := "2.13.7"

libraryDependencies += "com.propensive" %% "magnolia"      % "0.17.0"
libraryDependencies += "org.scala-lang"  % "scala-reflect" % scalaVersion.value % Provided
libraryDependencies += "org.tpolecat"   %% "skunk-core"    % "0.2.2"
libraryDependencies += "com.chuusai"    %% "shapeless"     % "2.3.7"

scalacOptions ++= Seq(
  "-Ymacro-annotations"
)
