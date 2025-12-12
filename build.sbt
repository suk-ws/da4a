aether.AetherKeys.aetherOldVersionMethod := true

ThisBuild / scalaVersion := "3.7.3"

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
val javaTarget = "12"
val javaTarget_scala = javaTarget

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
		
		Compile / doc / scalacOptions ++= Seq(
			"-private", // ?
		),
		
		scalacOptions ++= Seq(
//			"-language:postfixOps", // enable postfixOps (a.b => a b)
			"-language:experimental.macros", // enable macros feature
			"-language:experimental.saferExceptions", // enable throws keyword for methods
//			"-language:implicitConversions", // allow scala2 implicit conversion definition
//			                                 // suppress auto conversion warnings
			"-language:noAutoTupling", // disable auto tupling ( a(1, 2) => a((1, 2)) )
			"-language:strictEquality", // errors when comparing val with non-related type
			"-Yexplicit-nulls", // explicit null check, T|Null != T
			"-Wsafe-init", // error on check init
//			"-Xkind-projector", // enable https://github.com/typelevel/kind-projector (?)
			"-unchecked", // warnings when code is based on assumptions
			"-explain", // explain error in more detail
			"-explain-types", // explain type error in more detail
			"-deprecation", // show deprecated warnings
			"-feature", // show feature warnings
			"-encoding", encoding,
			"-release", javaTarget_scala,
		),
		javacOptions ++= Seq(
			"-Xlint:deprecation", // show deprecated warnings
			"-encoding", encoding,
			"-source", javaTarget,
			"-target", javaTarget,
		),
		
		autoAPIMappings := true,
		
		publishTo := ProjectMetadata.publishTo,
		credentials ++= ProjectMetadata.publishCredentials,
		
	)
