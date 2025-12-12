package cc.sukazyo.std
package command

trait IInputCommand {
	
	val command: String
	val args: Array[String]
	val argsRaw: String
	
	def subcommand: Option[IInputCommand]
	
}
