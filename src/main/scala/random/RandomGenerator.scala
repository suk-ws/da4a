package cc.sukazyo.std
package random

trait RandomGenerator

object RandomGenerator {
	
	trait CanGenerateDouble extends RandomGenerator:
		def nextDouble: Double
	
}
