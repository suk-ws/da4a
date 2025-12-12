package cc.sukazyo.std
package command

class TestInputCommandParser extends BaseTestsSuite {
	
	"while parsing command input from string" - {
		
		lazy val DefaultParser = InputCommandParser.default
		def defaultBuilder = InputCommandParser.Builder()
		
		raw"args should be separated by (\u0020) ascii-space" in :
			DefaultParser.parse("a b c delta e").args `shouldEqual` Array("a", "b", "c", "delta", "e")
		"args should not be separated by non-ascii spaces like full width spaces" in :
			DefaultParser.parse("tests ダタ　セト").args `shouldEqual` Array("tests", "ダタ　セト")
		"multiple ascii-spaces should not generate empty arg in middle" in :
			DefaultParser.parse("tests    some  of data").args `shouldEqual` Array("tests", "some", "of", "data")
		
		"""while some texts in quotes ('') or ("")""" - {
			
			"should be grouped as one argument no matter it contains separator or not" in {
				DefaultParser.parse("""tests 'data set'""").args `shouldEqual` Array("tests", "data set")
				DefaultParser.parse("""tests "data  set"""").args `shouldEqual` Array("tests", "data  set")
			}
			
			"mixes text and quotes" - {
				"should be allowed to be mixed in one arg" in {
					DefaultParser.parse("""tests some:'data set'.message is""").args `shouldEqual` Array("tests", "some:data set.message", "is")
					DefaultParser.parse("""tests "arg1 x":'arg2 y' param""").args `shouldEqual` Array("tests", "arg1 x:arg2 y", "param")
				}
			}
			
			"with quotes not closed" - {

				"should treat the rest of text as one arg in default" in {
					DefaultParser.parse("""use 'it like something""").args `shouldEqual` Array("use", "it like something")
				}
				
				"should throws IllegalArgumentException when allowUnclosedQuotes is false" in {
					an[IllegalArgumentException] should be thrownBy
						defaultBuilder.setAllowUnclosedQuotes(false).build()
							.parse("""use 'it like something""")
				}
				
			}
			
		}
		
		"tabs in text" - {
			
			"should be treated as separator (just like ascii-spaces) in default" in {
				DefaultParser.parse("parsed some\tdata").args `shouldEqual` Array("parsed", "some", "data")
			}
			
			"should be treated as normal character when tabAsSeparator is false" in {
				defaultBuilder.setTabAsSeparator(false).build()
					.parse("parsed some\tdata").args
						`shouldEqual` Array("parsed", "some\tdata")
			}
			
		}
		
//		"""texts nested '' should grouped in one arg""" in :
//			Cmd("""tests some:'data set'.message is""") shouldEqual Array("tests", "some:data set.message", "is")
//			Lmd("""tests some:'data set'.message is""") shouldEqual Array("tests", "some:data set.message", "is")
//		"""texts and "" nested '' should grouped in one arg""" in :
//			Cmd("""tests "arg1 x":'arg2 y' param""") shouldEqual Array("tests", "arg1 x:arg2 y", "param")
//			Lmd("""tests "arg1 x":"arg2 y" param""") shouldEqual Array("tests", "arg1 x:arg2 y", "param")
//		"with ' not closed" - {
//			whileStrict("should throws IllegalArgumentException") in :
//				an[IllegalArgumentException] should be thrownBy Cmd("""use 'it """)
//			whileLossy("should be cut at end") in :
//				Lmd("use 'it ") shouldEqual Array("use", "it ")
//		}
//		"""mixed ' and """" - {
//			whileStrict("should throws IllegalArgumentsException") in :
//				an[IllegalArgumentException] should be thrownBy Cmd("""tests "data set' "of it'""")
//			whileLossy("should be seen as a normal character") in :
//				Lmd("""tests "data set' "of it'""") shouldEqual Array("tests", "data set' of", "it")
//		}
//
//		raw"\ should escape itself" in :
//			Cmd(raw"input \\data") shouldEqual Array("input", "\\data")
//			Lmd(raw"input \\data") shouldEqual Array("input", "\\data")
//		raw"\ should escape ascii-space, makes it processed as a normal character" in :
//			Cmd(raw"input data\ set") shouldEqual Array("input", "data set")
//			Lmd(raw"input data\ set") shouldEqual Array("input", "data set")
//		raw"\ should escape ascii-space, makes it can be an arg body" in :
//			Cmd(raw"input \  some-thing") shouldEqual Array("input", " ", "some-thing")
//			Lmd(raw"input \  some-thing") shouldEqual Array("input", " ", "some-thing")
//		raw"""\ should escape "", makes it processed as a normal character""" in :
//			Cmd(raw"""use \"inputted""") shouldEqual Array("use", "\"inputted")
//			Lmd(raw"""use \"inputted""") shouldEqual Array("use", "\"inputted")
//		raw"\ should escape '', makes it processed as a normal character" in :
//			Cmd(raw"use \'inputted") shouldEqual Array("use", "'inputted")
//			Lmd(raw"use \'inputted") shouldEqual Array("use", "'inputted")
//		raw"\ should escape itself which inside a quoted scope" in :
//			Cmd(raw"use 'quoted \\ body'") shouldEqual Array("use", "quoted \\ body")
//			Lmd(raw"use 'quoted \\ body'") shouldEqual Array("use", "quoted \\ body")
//		raw"""\ should escape " which inside a "" scope""" in :
//			Cmd(raw"""in "quoted \" body" body""") shouldEqual Array("in", "quoted \" body", "body")
//			Lmd(raw"""in "quoted \" body" body""") shouldEqual Array("in", "quoted \" body", "body")
//		raw"""\ should escape ' which inside a "" scope""" in :
//			Cmd(raw"""in "not-quoted \' body" body""") shouldEqual Array("in", "not-quoted ' body", "body")
//			Lmd(raw"""in "not-quoted \' body" body""") shouldEqual Array("in", "not-quoted ' body", "body")
//		raw"""\ should escape ' which inside a '' scope""" in :
//			Cmd(raw"""in 'quoted \' body' body""") shouldEqual Array("in", "quoted ' body", "body")
//			Lmd(raw"""in 'quoted \' body' body""") shouldEqual Array("in", "quoted ' body", "body")
//		raw"""\ should escape " which inside a ' scope""" in :
//			Cmd(raw"""in 'not-quoted \" body' body""") shouldEqual Array("in", "not-quoted \" body", "body")
//			Lmd(raw"""in 'not-quoted \" body' body""") shouldEqual Array("in", "not-quoted \" body", "body")
//		raw"\ should not escape ascii-space which inside a quoted scope" in :
//			Cmd(raw"""'quoted \ do not escape' did""") shouldEqual Array(raw"quoted \ do not escape", "did")
//			Lmd(raw"""'quoted \ do not escape' did""") shouldEqual Array(raw"quoted \ do not escape", "did")
//		raw"with \ in the end" - {
//			whileStrict("should throws IllegalArgumentException") in :
//				an[IllegalArgumentException] should be thrownBy Cmd("something error!\\")
//			whileLossy("should seen as normal char") in :
//				Lmd("something error!\\") shouldEqual Array("something", "error!\\")
//		}
//
//		"with multi-line input" - {
//			whileStrict("should throws IllegalArgumentException") in :
//				an[IllegalArgumentException] should be thrownBy Cmd("something will\nhave a new line")
//			whileLossy("should keep new-line char origin like") in :
//				Lmd("something will\nhave a new line") shouldEqual Array("something", "will\nhave", "a", "new", "line")
//		}
//
//		"empty string input should return empty array" in {
//			Cmd("") shouldEqual Array.empty[String]
//			Lmd("") shouldEqual Array.empty[String]
//		}
//
//		val example_special_character = Table(
//			"char",
//			"　",
//			"\\t",
//			"\\a",
//			"/",
//			"&&",
//			"\\u1234",
//		)
//		forAll(example_special_character) { char =>
//			s"input with special character ($char) should keep origin like" in {
//				Cmd(s"$char dataset data[$char]contains parsed") shouldEqual
//					Array(char, "dataset", s"data[$char]contains", "parsed")
//				Lmd(s"$char dataset data[$char]contains parsed") shouldEqual
//					Array(char, "dataset", s"data[$char]contains", "parsed")
//			}
//		}
	}
	
	//########################
	//  The followings are the classic parser tests
	//
	
	"For classic parsers wrappers" - {
		
		def Cmd (input: String): Array[String] =
			InputCommandParser.classicStrictParser.parse(input).args
		def Lmd (input: String): Array[String] =
			InputCommandParser.classicLossyParser.parse(input).args
		
		def whileLossy (info: String): String = "in lossy mode " + info
		def whileStrict (info: String): String = "in strict mode" + info
		
		raw"args should be separated by (\u0020) ascii-space" in :
			Cmd("a b c delta e") shouldEqual Array("a", "b", "c", "delta", "e")
			Lmd("a b c delta e") shouldEqual Array("a", "b", "c", "delta", "e")
		"args should not be separated by non-ascii spaces" in :
			Cmd("tests ダタ　セト") shouldEqual Array("tests", "ダタ　セト")
			Lmd("tests ダタ　セト") shouldEqual Array("tests", "ダタ　セト")
		"multiple ascii-spaces should not generate empty arg in middle" in :
			Cmd("tests    some  of data") shouldEqual Array("tests", "some", "of", "data")
			Lmd("tests    some  of data") shouldEqual Array("tests", "some", "of", "data")
		
		"""texts and ascii-spaces in '' should grouped in one arg""" in :
			Cmd("""tests 'data set'""") shouldEqual Array("tests", "data set")
			Lmd("""tests 'data set'""") shouldEqual Array("tests", "data set")
			Cmd("""'rich command' arg1 arg2""") shouldEqual Array("rich command", "arg1", "arg2")
			Lmd("""'rich command' arg1 arg2""") shouldEqual Array("rich command", "arg1", "arg2")
		"""texts and ascii-spaces in "" should grouped in one arg""" in :
			Cmd("""tests "data  set"""") shouldEqual Array("tests", "data  set")
			Lmd("""tests "data  set"""") shouldEqual Array("tests", "data  set")
		"""texts nested '' should grouped in one arg""" in :
			Cmd("""tests some:'data set'.message is""") shouldEqual Array("tests", "some:data set.message", "is")
			Lmd("""tests some:'data set'.message is""") shouldEqual Array("tests", "some:data set.message", "is")
		"""texts and "" nested '' should grouped in one arg""" in :
			Cmd("""tests "arg1 x":'arg2 y' param""") shouldEqual Array("tests", "arg1 x:arg2 y", "param")
			Lmd("""tests "arg1 x":"arg2 y" param""") shouldEqual Array("tests", "arg1 x:arg2 y", "param")
		"with ' not closed" - {
			whileStrict("should throws IllegalArgumentException") in :
				an[IllegalArgumentException] should be thrownBy Cmd("""use 'it """)
			whileLossy("should be cut at end") in :
				Lmd("use 'it ") shouldEqual Array("use", "it ")
		}
		"""mixed ' and """" - {
			whileStrict("should throws IllegalArgumentsException") in :
				an[IllegalArgumentException] should be thrownBy Cmd("""tests "data set' "of it'""")
			whileLossy("should be seen as a normal character") in :
				Lmd("""tests "data set' "of it'""") shouldEqual Array("tests", "data set' of", "it")
		}
		
		raw"\ should escape itself" in :
			Cmd(raw"input \\data") shouldEqual Array("input", "\\data")
			Lmd(raw"input \\data") shouldEqual Array("input", "\\data")
		raw"\ should escape ascii-space, makes it processed as a normal character" in :
			Cmd(raw"input data\ set") shouldEqual Array("input", "data set")
			Lmd(raw"input data\ set") shouldEqual Array("input", "data set")
		raw"\ should escape ascii-space, makes it can be an arg body" in :
			Cmd(raw"input \  some-thing") shouldEqual Array("input", " ", "some-thing")
			Lmd(raw"input \  some-thing") shouldEqual Array("input", " ", "some-thing")
		raw"""\ should escape "", makes it processed as a normal character""" in :
			Cmd(raw"""use \"inputted""") shouldEqual Array("use", "\"inputted")
			Lmd(raw"""use \"inputted""") shouldEqual Array("use", "\"inputted")
		raw"\ should escape '', makes it processed as a normal character" in :
			Cmd(raw"use \'inputted") shouldEqual Array("use", "'inputted")
			Lmd(raw"use \'inputted") shouldEqual Array("use", "'inputted")
		raw"\ should escape itself which inside a quoted scope" in :
			Cmd(raw"use 'quoted \\ body'") shouldEqual Array("use", "quoted \\ body")
			Lmd(raw"use 'quoted \\ body'") shouldEqual Array("use", "quoted \\ body")
		raw"""\ should escape " which inside a "" scope""" in :
			Cmd(raw"""in "quoted \" body" body""") shouldEqual Array("in", "quoted \" body", "body")
			Lmd(raw"""in "quoted \" body" body""") shouldEqual Array("in", "quoted \" body", "body")
		raw"""\ should escape ' which inside a "" scope""" in :
			Cmd(raw"""in "not-quoted \' body" body""") shouldEqual Array("in", "not-quoted ' body", "body")
			Lmd(raw"""in "not-quoted \' body" body""") shouldEqual Array("in", "not-quoted ' body", "body")
		raw"""\ should escape ' which inside a '' scope""" in :
			Cmd(raw"""in 'quoted \' body' body""") shouldEqual Array("in", "quoted ' body", "body")
			Lmd(raw"""in 'quoted \' body' body""") shouldEqual Array("in", "quoted ' body", "body")
		raw"""\ should escape " which inside a ' scope""" in :
			Cmd(raw"""in 'not-quoted \" body' body""") shouldEqual Array("in", "not-quoted \" body", "body")
			Lmd(raw"""in 'not-quoted \" body' body""") shouldEqual Array("in", "not-quoted \" body", "body")
		raw"\ should not escape ascii-space which inside a quoted scope" in :
			Cmd(raw"""'quoted \ do not escape' did""") shouldEqual Array(raw"quoted \ do not escape", "did")
			Lmd(raw"""'quoted \ do not escape' did""") shouldEqual Array(raw"quoted \ do not escape", "did")
		raw"with \ in the end" - {
			whileStrict("should throws IllegalArgumentException") in :
				an[IllegalArgumentException] should be thrownBy Cmd("something error!\\")
			whileLossy("should seen as normal char") in :
				Lmd("something error!\\") shouldEqual Array("something", "error!\\")
		}
		
		"with multi-line input" - {
			whileStrict("should throws IllegalArgumentException") in :
				an[IllegalArgumentException] should be thrownBy Cmd("something will\nhave a new line")
			whileLossy("should keep new-line char origin like") in :
				Lmd("something will\nhave a new line") shouldEqual Array("something", "will\nhave", "a", "new", "line")
		}
		
		"empty string input should return empty array" in {
			Cmd("") shouldEqual Array.empty[String]
			Lmd("") shouldEqual Array.empty[String]
		}
		
		"string with only spaces should return empty array" in {
			Cmd("       ") shouldEqual Array.empty[String]
			Lmd("       ") shouldEqual Array.empty[String]
		}
		
		val example_special_character = Table(
			"char",
			"　",
			"\t",
			"\\t",
			"\\a",
			"/",
			"&&",
			"\\u1234",
		)
		forAll(example_special_character) { char =>
			s"input with special character ($char) should keep origin like" in {
				Cmd(s"$char dataset data[$char]contains parsed") shouldEqual
					Array(char, "dataset", s"data[$char]contains", "parsed")
				Lmd(s"$char dataset data[$char]contains parsed") shouldEqual
					Array(char, "dataset", s"data[$char]contains", "parsed")
			}
		}
		
	}
}
