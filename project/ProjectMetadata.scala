import sbt.*

object ProjectMetadata {
	
	val package_name: String = Properties.package_name
	val package_group: String = Properties.package_group
	val package_id: String = Properties.package_id
	
	val isSnapshot: Boolean = Properties.SNAPSHOT
	
	val version: String = Seq(
		Properties.version,
		if (isSnapshot) "SNAPSHOT" else null
	).filterNot(x => x == null).mkString("-")
	
	val resolvers: Seq[Resolver] = Properties.dependencyRepos
	val dependencies: Seq[ModuleID] = Properties.dependencies
	
	val publishTo: Option[Resolver] = Properties.publishTo
	val publishCredentials: Seq[Credentials] = Properties.publishCredentials
	
}
