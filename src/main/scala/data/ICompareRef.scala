/** This have been deprecated due to this have been implemented by Scala.
  */

package cc.sukazyo.std
package data

//trait ICompareRef [T <: AnyRef] (data: T) {
//
//	/** Get this object's identity hash code.
//	  *
//	  * This hashcode should not be override by the class.
//	  *
//	  * It should always returns the same value on the same jvm object, no matter if its inner
//	  * status. And it should returns different values in different jvm object, even theirs
//	  * [[equals]] is true.
//	  *
//	  * But due to it is just a *hash code*, so it has the danger of collision makes it not
//	  * absolutely unique. So it may returns the same value for two different object in a very
//	  * unlucky situations.
//	  *
//	  * @see [[System.identityHashCode]] the provider of object's identity hash code
//	  */
//	final def identityHashCode: Int =
//		System.identityHashCode(data)
//
//	/** Compare two objects by their identity hash code.
//	  *
//	  * This is relatively equals to `a == b` in native java, means it will compare if the two
//	  * objects given is **the same object in the jvm**.
//	  *
//	  * But due to it is just a *hash code*, so it has the danger of collision makes it not
//	  * absolutely unique. So it may occurs false-positive in a very unlucky situations.
//	  *
//	  * Take care of if this method (or [[identityHashCode]] method) have been overwritten,
//	  * this may cause this method cannot works as expected.
//	  *
//	  * @see [[identityHashCode]] the provider of object's identity hash code
//	  *
//	  * @param other Another object that want to compare with this object
//	  * @return `true` if the two objects are **the same object in the jvm**, false otherwise.
//	  *
//	  *         Or in some very unlucky situations, it may return `true` even the two objects
//	  *         are not the same.
//	  */
//	infix final def sameAs (other: AnyRef): Boolean =
//		data.identityHashCode == other.identityHashCode
//	/** Compare two objects by their identity hash code.
//	  *
//	  * This is relatively equals to `a == b` in native java, means it will compare if the two
//	  * objects given is **the same object in the jvm**.
//	  *
//	  * But due to it is just a *hash code*, so it has the danger of collision makes it not
//	  * absolutely unique. So it may occurs false-positive in a very unlucky situations.
//	  *
//	  * Take care of if this method (or [[identityHashCode]] method) have been overwritten,
//	  * this may cause this method cannot works as expected.
//	  *
//	  * @see [[identityHashCode]] the provider of object's identity hash code
//	  *
//	  * @param other Another object (aka. rvalue) that want to compare with this object (aka.
//	  *              lvalue)
//	  * @return `true` if the two objects are **the same object in the jvm**, false otherwise.
//	  *
//	  *         Or in some very unlucky situations, it may return `true` even the two objects
//	  *         are not the same.
//	  */
//	infix final def =#= (other: AnyRef): Boolean =
//		data sameAs other
//	/** Compare two objects by their identity hash code.
//	  *
//	  * This is relatively equals to `a != b` in native java, means it will compare if the two
//	  * objects given is **the same object in the jvm**.
//	  *
//	  * But due to it is just a *hash code*, so it has the danger of collision makes it not
//	  * absolutely unique. So it may occurs false-positive in a very unlucky situations.
//	  *
//	  * Take care of if this method (or [[identityHashCode]] method) have been overwritten,
//	  * this may cause this method cannot works as expected.
//	  *
//	  * @see [[identityHashCode]] the provider of object's identity hash code
//	  *
//	  * @param other Another object (aka. rvalue) that want to compare with this object (aka.
//	  *              lvalue)
//	  * @return `true` if the two objects are NOT **the same object in the jvm**, false otherwise.
//	  *
//	  *         Or in some very unlucky situations, it may return `false` even the two objects
//	  *         are not the same.
//	  */
//	infix final def =!= (other: AnyRef): Boolean =
//		!(data sameAs other)
//
//}
