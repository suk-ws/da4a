package cc.sukazyo.std
package data

class TestEncapsulateValue extends BaseDataTestSuite {
	
	"any data" - {
		
		"can be encapsulated to" - {
			
			"a Some using .toSome extension" in {
				val data = 42
				val encapsulated = data.toSome
				encapsulated shouldBe a [Some[?]]
				encapsulated shouldEqual Some(42)
			}
			
			"an Option using .toOption extension" in {
				val data = "String"
				val encapsulated = data.toOption
				encapsulated shouldBe a [Option[?]]
				encapsulated shouldEqual Some("String")
			}
			
			"an Either" - {
				
				"Left which itself is left-value using .toLeft extension" in {
					val data = 42
					val encapsulated = data.toLeft
					assertCompiles("val left: Left[Int, String] = data.toLeft")
					encapsulated shouldBe a [Left[?, ?]]
					encapsulated shouldEqual Left(42)
				}
				
				"Right which itself is right-value using .toRight extension" in {
					val data = "String"
					val encapsulated = data.toRight
					assertCompiles("val right: Right[Int, String] = data.toRight")
					encapsulated shouldBe a [Right[?, ?]]
					encapsulated shouldEqual Right("String")
				}
				
				"which itself is left-value using .toEitherLeft extension" in {
					val data = 42
					val encapsulated = data.toEitherLeft
					assertCompiles("val either: Either[Int, String] = data.toEitherLeft")
					encapsulated shouldBe a [Either[?, ?]]
					encapsulated shouldEqual Left(42)
				}
				
				"which itself is right-value using .toEitherRight extension" in {
					val data = "String"
					val encapsulated = data.toEitherRight
					assertCompiles("val either: Either[Int, String] = data.toEitherRight")
					encapsulated shouldBe a [Either[?, ?]]
					encapsulated shouldEqual Right("String")
				}
				
			}
			
		}
		
	}
	
}
