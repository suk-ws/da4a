//package cc.sukazyo.std
//package data
//
//class TestCompareRef extends BaseDataTestSuite {
//	
//	"when using sameAs or =#= operator comparing two objects" - {
//		val obj = "aaa"
//		val objRef = obj
//		
//		//noinspection ScalaUnusedExpression
//		"which is the same object in jvm should returns true" in {
//			obj sameAs objRef shouldBe true
//			obj === objRef shouldBe true
//			obj =!= objRef shouldBe false
//		}
//		
//		//noinspection ScalaUnusedExpression
//		"which is different object in jvm should returns false, even they are Objects.equals" in {
//			val objNew = new String("aaa")
//			obj shouldEqual objNew
//			obj sameAs objNew shouldBe false
//			obj =#= objNew shouldBe false
//			obj =!= objNew shouldBe true
//		}
//		
//		//noinspection ScalaUnusedExpression
//		"which is totally different object should returns false" in {
//			val objOnSome = obj.toSome
//			obj sameAs objOnSome shouldBe false
//			obj =#= objOnSome shouldBe false
//			obj =!= objOnSome shouldBe true
//		}
//		
//	}
//	
//}
