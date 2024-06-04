aether.AetherKeys.aetherOldVersionMethod := true

ThisBuild / scalaVersion := "3.4.1"

ThisBuild / organization := "cc.sukazyo"
ThisBuild / organizationName := "Sukazyo Workshop"
ThisBuild / organizationHomepage := Some(url("https://sukazyo.cc"))
ThisBuild / developers := List(
	Developer(
		id = "Eyre_S",
		name = "A.C.Sukazyo Eyre",
		email = "sukazyo@outlook.com",
		url = url("https://sukazyo.sukazyo.cc")
	)
)

ThisBuild / licenses += "MIT" -> url("https://github.com/suk-ws/da4a/blob/master/LICENSE")

ThisBuild / version := ProjectMetadata.version
ThisBuild / versionScheme := Some("semver-spec")

ThisBuild / resolvers ++= ProjectMetadata.resolvers

val encoding = "UTF-8"
val javaTarget = "1.8"

lazy val root = (project in file("."))
	.settings(
		
		name := "da4a",
		description :=
			"""The Standard Library Extension from Sukazyo Workshop.""".stripMargin,
		idePackagePrefix := Some("cc.sukazyo.da4a"),
		
		crossPaths := false,
		
		moduleName := ProjectMetadata.package_name,
		idePackagePrefix := Some({ProjectMetadata.package_id}),
		
		libraryDependencies ++= ProjectMetadata.dependencies,
		
		scalacOptions ++= Seq(
			"-language:postfixOps",
			"-language:experimental.macros",
			"-language:implicitConversions",
			"-language:noAutoTupling",
			"-language:canThrow",
			"-Yexplicit-nulls",
			"-Ysafe-init",
			"-unchecked",
			"-explain-types",
			"-encoding", encoding,
		),
		javacOptions ++= Seq(
			"-encoding", encoding,
			"-source", javaTarget,
			"-target", javaTarget,
		),
		
		autoAPIMappings := true,
		
		publishTo := ProjectMetadata.publishTo,
		credentials ++= ProjectMetadata.publishCredentials,
		
	)
