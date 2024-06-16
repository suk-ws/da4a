package cc.sukazyo.std
package data

trait IEncapsulateValue [T] (data: T) {
	
	/** Convert this value `x: T` to a <code>[[Some]][T](x)</code>.
	  *
	  * The type `T` will be auto-deduced using the value x.
	  * 
	  * @since 0.1.0
	  */
	def toSome: Some[T] = Some(data)
	
	/** Convert this value `x: T` to a <code>[[Some]][T](x)</code>, but with the type
	  * <code>[[Option]][T]</code> returned.
	  *
	  * The type `T` will be auto-deduced using the value x.
	  * 
	  * @since 0.1.0
	  */
	def toOption: Option[T] = this.toSome
	
	/** Convert this value `x: T` to a <code>[[Left]][T, R](x)</code>.
	  * 
	  * The type `T` will be auto-deduced using the value x.
	  * 
	  * @tparam R The type of the [[Right]] value should be.
	  *           
	  *           It can be left empty, if so, the preferred type is [[Nothing]], makes it
	  *           should be able to match any types.
	  * 
	  * @since 0.1.0
	  */
	def toLeft[R]: Left[T, R] = Left(data)
	
	/** Convert this value `x: T` to a <code>[[Right]][L, T](x)</code>.
	  *
	  * The type `T` will be auto-deduced using the value x.
	  *
	  * @tparam L The type of the [[Left]] value should be.
	  *
	  *           It can be left empty, if so, the preferred type is [[Nothing]], makes it
	  *           should be able to match any types.
	  *
	  * @since 0.1.0
	  */
	def toRight[L]: Right[L, T] = Right(data)
	
	/** Convert this value `x: T` to a <code>[[Left]][T, R](x)</code>, but with the type <code>
	  * [[Either]][T, R]</code> returned.
	  *
	  * The type `T` will be auto-deduced using the value x.
	  *
	  * @tparam R The type of the [[Right]] value should be.
	  *
	  *           It can be left empty, if so, the preferred type is [[Nothing]], makes it
	  *           should be able to match any types.
	  *
	  * @since 0.1.0
	  */
	def toEitherLeft[R]: Either[T, R] = this.toLeft
	
	/** Convert this value `x: T` to a <code>[[Right]][L, T](x)</code>, but with the type <code>
	  * [[Either]][T, R]</code> returned.
	  *
	  * The type `T` will be auto-deduced using the value x.
	  *
	  * @tparam L The type of the [[Left]] value should be.
	  *
	  *           It can be left empty, if so, the preferred type is [[Nothing]], makes it
	  *           should be able to match any types.
	  *
	  * @since 0.1.0
	  */
	def toEitherRight[L]: Either[L, T] = this.toRight
	
}
