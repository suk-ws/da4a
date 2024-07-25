package cc.sukazyo.std
package contexts

import org.scalatest.Assertion

// TODO: error message related test have not been tested
class TestGivenContext extends BaseTestsSuite {
	
	"a context" - {
		
		"should be able to created" - {
			
			"using new keyword" in {
				assertCompiles("val a: GivenContext = new GivenContext()")
			}
			"using apply method" in {
				assertCompiles("val a: GivenContext = GivenContext.apply()")
				assertCompiles("val a: GivenContext = GivenContext()")
			}
			
		}
		
		"just created" - {
			val newContext = GivenContext()
			"should be empty" - {
				"in total" in {
					/*_*/newContext.size shouldBe 0/*_*/
					/*_*/newContext.isEmpty shouldBe true/*_*/
					newContext.nonEmpty shouldBe false
				}
				"in global scope" in {
					newContext.sizeGlobal shouldBe 0
					newContext.isEmptyGlobal shouldBe true
					newContext.nonEmptyGlobal shouldBe false
				}
				"in every owned scope" in {
					newContext.ownedScopes.foreach { scope =>
						scope.size shouldBe 0
						scope.isEmpty shouldBe true
						scope.nonEmpty shouldBe false
					}
				}
			}
		}
		
		"should be able to get owned scopes" - {
			
			"specifically owned by one Class" - {
				"using ownedBy method" in:
					val _c = GivenContext()
					val stringOwned = _c.ownedBy[String]
					stringOwned.isOwnedBy(classOf[String])
				"using ownedBy operation `/` with Class[?] tag" in:
					val _c = GivenContext()
					val stringOwned = _c / classOf[String]
					stringOwned.isOwnedBy(classOf[String])
				"using ownedBy operation `/` with Class's instance" in:
					val _c = GivenContext()
					val stringOwned = _c / "string"
					stringOwned.isOwnedBy(classOf[String])
			}
			
		}
		
		"added one variable" - {
			"to global scope" - {
				val _c = GivenContext()
				_c << "string"
				"should make global scope not empty" in {
					_c.sizeGlobal shouldBe 1
					_c.isEmptyGlobal shouldBe false
					_c.nonEmptyGlobal shouldBe true
				}
				"should make all scopes not empty" in {
					_c.size shouldBe 1
					_c.isEmpty shouldBe false
					_c.nonEmpty shouldBe true
				}
				"should not effects owned scopes" in {
					_c.ownedScopes.size shouldBe 0
				}
			}
			"to owned scopes" - {
				val _c = GivenContext()
				_c / "string" << "string"
				"should make all scopes not empty" in {
					_c.size shouldBe 1
					_c.isEmpty shouldBe false
					_c.nonEmpty shouldBe true
				}
				"should not effects global scope" in {
					_c.sizeGlobal shouldBe 0
					_c.isEmptyGlobal shouldBe true
					_c.nonEmptyGlobal shouldBe false
				}
				"should create a new string typed owned scope" in {
					_c.ownedScopes.size shouldBe 1
					_c.ownedScopes.head.isOwnedBy[String] shouldBe true
				}
			}
		}
		
		"should be able to take out a value" - {
			import GivenContext.CxtOption
			
			"in global scope" - {
				val _c = GivenContext()
				_c << "string"
				"using use method" in:
					assertCompiles("val x: CxtOption[String] = _c.use[String]")
					_c.use[String] shouldEqual Right("string")
				"using use operation `>>`" in:
					assertCompiles("val x: CxtOption[String] = _c >> classOf[String]")
					_c >> classOf[String] shouldEqual Right("string")
				"using use callback" in:
					assertCompiles("_c.use[String, String|Null](_.toUpperCase)")
					_c.use[String, Assertion](_ shouldEqual "string")
				"using use callback operation `>>`" in:
					assertCompiles("_c >> { (x: String) => x.toUpperCase }")
					_c >> { (x: String) => x shouldEqual "string"}
				"using tryUse operation `>!>`" in:
					assertCompiles("val x: String = _c >!> classOf[String]")
					noException should be thrownBy {
						_c >!> classOf[String] shouldEqual "string"
					}
				"using consume callback" in:
					assertCompiles("_c.consume[String](_.toUpperCase)")
					_c.consume[String](_ shouldEqual "string")
			}
			
			"in a owned scope" - {
				val _c = GivenContext()
				_c / "owner-string" << "string"
				val _owp = _c / classOf[String]
				"using use method" in:
					assertCompiles("val x: CxtOption[String] = _owp.use[String]")
					_owp.use[String] shouldEqual Right("string")
				"using use operation `>>`" in:
					assertCompiles("val x: CxtOption[String] = _owp >> classOf[String]")
					_owp >> classOf[String] shouldEqual Right("string")
				"using use callback" in:
					assertCompiles("_owp.use[String, String|Null](_.toUpperCase)")
					_owp.use[String, Assertion](_ shouldEqual "string")
				"using use callback operation `>>`" in:
					assertCompiles("_owp >> { (x: String) => x.toUpperCase }")
					_owp >> { (x: String) => x shouldEqual "string"}
				"using tryUse operation `>!>`" in:
					assertCompiles("val x: String = _owp >!> classOf[String]")
					noException should be thrownBy {
						_owp >!> classOf[String] shouldEqual "string"
					}
				"using consume callback" in:
					assertCompiles("_owp.consume[String](_.toUpperCase)")
					_owp.consume[String](_ shouldEqual "string")
			}
			
		}
		
		"added two variables with the same type" - {
			val _c = GivenContext()
			_c << "old one"
			_c << "new one"
			
			"should be override by the last one" in {
				_c.size shouldBe 1
				_c.isEmpty shouldBe false
				_c.nonEmpty shouldBe true
				_c.use[String] shouldEqual Right("new one")
			}
			
		}
		
		"should separate parent and child type" - {
			
			"in global scope variables" in {
				val _c = GivenContext()
				_c << ChildType(2)
				_c.use[ParentType].isRight shouldBe false
				_c.use[ChildType] shouldEqual Right(ChildType(2))
				_c << ParentType(1)
				_c.use[ParentType] shouldEqual Right(ParentType(1))
				_c.use[ChildType] shouldEqual Right(ChildType(2))
			}
			
			"in owned scope keys" in {
				val _c = GivenContext()
				_c / ChildType(1) << "data"
				_c.ownedBy[ParentType].use[String].isRight shouldBe false
				_c.ownedBy[ChildType].use[String] shouldEqual Right("data")
				_c / ParentType(1) << "parent-data"
				_c.ownedBy[ParentType].use[String] shouldEqual Right("parent-data")
				_c.ownedBy[ChildType].use[String] shouldEqual Right("data")
			}
			
			"in owned scope variables" in {
				val _c = GivenContext()
				_c / "string" << ChildType(2)
				_c.ownedBy[String].use[ParentType].isRight shouldBe false
				_c.ownedBy[String].use[ChildType] shouldEqual Right(ChildType(2))
				_c / "string" << ParentType(1)
				_c.ownedBy[String].use[ParentType] shouldEqual Right(ParentType(1))
				_c.ownedBy[String].use[ChildType] shouldEqual Right(ChildType(2))
			}
			
		}
		
		"should be able to add a variable with its parent Class" - {
			
			"in the global scope" - {
				"using provide method" in {
					val _c = GivenContext()
					_c.provide[ParentType](ChildType(25565))
					_c.use[ChildType].isRight shouldBe false
					_c.use[ParentType] shouldEqual Right(ChildType(25565))
				}
				"using provide operation `<<` with a pair" in {
					val _c = GivenContext()
					_c << (classOf[ParentType] -> ChildType(12333))
					_c.use[ChildType].isRight shouldBe false
					_c.use[ParentType] shouldEqual Right(ChildType(12333))
				}
			}
			"in a owned scope" - {
				"using provide method" in {
					val _c: GivenContext#OwnedContext = GivenContext() / classOf[String]
					_c.provide[ParentType](ChildType(25565))
					_c.use[ChildType].isRight shouldBe false
					_c.use[ParentType] shouldEqual Right(ChildType(25565))
				}
				"using provide operation `<<` with a pair" in {
					val _c: GivenContext#OwnedContext = GivenContext() / classOf[String]
					_c << (classOf[ParentType] -> ChildType(12333))
					_c.use[ChildType].isRight shouldBe false
					_c.use[ParentType] shouldEqual Right(ChildType(12333))
				}
			}
			
		}
		
		"should be able to return a value with callback use" - {
			
			case class AString (s: String)
			
			"in global scope" - {
				val _have: GivenContext = GivenContext()
				_have << AString("my data")
				val _havent: GivenContext = GivenContext()
				"using use callback" in {
					val have_res_opt: GivenContext#ConsumeResult[String] = _have.use { (s: AString) => s.s.toUpperCase().nn }
					val have_res: String|Boolean = have_res_opt || { false }
					val have_res2: String|Boolean = have_res_opt.orElse { false }
					val havent_res_opt: GivenContext#ConsumeResult[String] = _havent.use { (s: AString) => s.s.toUpperCase().nn }
					val havent_res: String|Boolean = havent_res_opt || { false }
					val havent_res2: String|Boolean = havent_res_opt.orElse { false }
					have_res_opt.toOption shouldEqual Some("MY DATA")
					(have_res_opt|?) shouldEqual Some("MY DATA")
					have_res shouldEqual "MY DATA"
					have_res2 shouldEqual "MY DATA"
					havent_res_opt.toOption shouldEqual None
					(havent_res_opt|?) shouldEqual None
					havent_res shouldEqual false
					havent_res2 shouldEqual false
				}
				"using use callback operation" in {
					val have_res: String|Boolean = _have >> { (s: AString) => s.s.toUpperCase().nn } || { false }
					val havent_res: String|Boolean = _havent >> { (s: AString) => s.s.toUpperCase().nn } || { false }
					have_res shouldEqual "MY DATA"
					havent_res shouldEqual false
				}
			}
			
		}
		
	}
	
	class ParentType (val id: Int):
		override def equals (obj: Any): Boolean =
			obj.toString == this.toString
		override def toString: String = s"ParentType($id)"
	class ChildType (id: Int) extends ParentType (id):
		override def toString: String = s"ChildType($id)"
	
}
