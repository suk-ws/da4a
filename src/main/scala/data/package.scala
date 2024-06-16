package cc.sukazyo.std

package object data {
	
	implicit class EncapsulateValue [T] (data: T) extends IEncapsulateValue[T](data)
	
}
