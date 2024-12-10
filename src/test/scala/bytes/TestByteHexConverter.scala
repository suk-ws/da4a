package cc.sukazyo.std
package bytes

import bytes.ByteHexConverter.*

class TestByteHexConverter extends BaseTestsSuite {
	
	"In ByteHexConverter" - {
		
		"Byte2Hex.toHex" - {
			
			"should convert byte 0 to hex string 00" in {
				0.toByte.toHex shouldEqual "00"
			}
			"should convert byte 15 to hex string 0f" in {
				15.toByte.toHex shouldEqual "0f"
			}
			"should convert byte -1 to hex string ff" in {
				-1.toByte.toHex shouldEqual "ff"
			}
			"should convert byte 127 to hex string 7f" in {
				127.toByte.toHex shouldEqual "7f"
			}
			"should convert byte -128 to hex string 80" in {
				-128.toByte.toHex shouldEqual "80"
			}
			
		}
		
		"ByteArray2Hex.toHex" - {
			
			"should convert byte array Array(0, 1, 2, 3) to hex string 00010203" in {
				Array[Byte](0, 1, 2, 3).toHex shouldEqual "00010203"
			}
			"should convert byte array Array(-1, -2, -3, -4) to hex string fffefdfc" in {
				Array[Byte](-1, -2, -3, -4).toHex shouldEqual "fffefdfc"
			}
			
		}
		
	}
	
}
