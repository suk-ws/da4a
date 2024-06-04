package cc.sukazyo.std

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers as ShouldMatchers
import org.scalatest.prop.TableDrivenPropertyChecks

class BaseTestsSuite
	extends AnyFreeSpec
		with ShouldMatchers
		with TableDrivenPropertyChecks
