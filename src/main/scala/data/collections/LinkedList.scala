package cc.sukazyo.std
package data.collections

import java.util.LinkedList as JLinkedList
import java.util.Iterator as JIterator
import java.util.ListIterator as JListIterator
import scala.collection.{mutable, *}
import scala.collection.generic.DefaultSerializable
import scala.collection.mutable.{ReusableBuilder, SeqOps}
import scala.jdk.CollectionConverters.*

/** LinkedList implementation for scala.
  * 
  * It wrappers the Java [[JLinkedList]] class, and provides a scala-like interface.
  * 
  * Comparing to the normal [[List]] or [[ListBuffer]], it implements some additional base
  * traits to provide more efficient linked list operations:
  * 
  * - [[InPlaceSortable]]: Sort the list in place.
  * - [[IteratingOps]]: Provides some additional operations, for doing some operations while in
  *   iteration.
  * 
  * @since 0.2.0
  * 
  * @tparam E Type of the elements in the collection.
  */
class LinkedList [E]
	extends mutable.AbstractBuffer[E]
		with InPlaceSortable[E]
		with IteratingOps[E]
		with mutable.SeqOps[E, LinkedList, LinkedList[E]]
		with StrictOptimizedSeqOps[E, LinkedList, LinkedList[E]]
		with mutable.ReusableBuilder[E, immutable.List[E]]
		with IterableFactoryDefaults[E, LinkedList]
		with DefaultSerializable {
	
	private val list: JLinkedList[E] = new JLinkedList[E]()
	
	override def iterator: Iterator[E] =
		list.iterator.nn.asScala
	override def iterableFactory: SeqFactory[LinkedList] =
		LinkedList
	
	override def result (): List[E] =
		list.asScala.toList
	
	override def apply (idx: Int): E =
		list.get(idx) match
			case null => throw new IndexOutOfBoundsException
			case value => value
	
	override def length: Int = list.size
	
	override def update(idx: Int, elem: E): Unit = {
		list.set(idx, elem)
	}
	
	override def insert (idx: Int, elem: E): Unit =
		list.add(idx, elem)
	override def insertAll (idx: Int, elems: IterableOnce[E]): Unit =
		list.addAll(idx, elems.iterator.toList.asJava)
	
	override def prepend (elem: E): LinkedList.this.type =
		list.addFirst(elem)
		this
	override def addOne (elem: E): LinkedList.this.type =
		list.add(elem)
		this
	
	override def remove (idx: Int): E =
		list.remove(idx).nn
	override def remove (idx: Int, count: Int): Unit =
		for (_ <- 1 to count)
			list.remove(idx)
	
	override def clear (): Unit =
		list.clear()
	
	override def patchInPlace (from: Int, patch: IterableOnce[E], replaced: Int): LinkedList.this.type =
		val newIter = patch.iterator
		val currIter = list.listIterator().nn
		currIter.next()
		for (_ <- 1 to from)
			currIter.next()
		for (_ <- 1 to replaced)
			currIter.set(newIter.next())
			currIter.next()
		this
	
	override def sortInPlace (implicit ord: Ordering[E]): LinkedList.this.type =
		list.sort(ord)
		this
	
	override def className: String = "LinkedList"
	
	private def freshFrom (iter: Iterator[E]): LinkedList.this.type =
		list.clear()
		iter.foreach(list.add)
		this
	
	override def removeIf (predicate: E => Boolean): LinkedList.this.type =
		val iter = list.listIterator.nn
		while iter.hasNext do
			if predicate(iter.next().nn) then
				iter.remove()
		this
	
}

/** Factories for [[LinkedList]].
  * 
  * @since 0.2.0
  */
object LinkedList extends StrictOptimizedSeqFactory[LinkedList] {
	
	override def from[A] (coll: collection.IterableOnce[A]): LinkedList[A] =
		new LinkedList[A].freshFrom(coll.iterator)
	
	override def newBuilder[A]: mutable.Builder[A, LinkedList[A]] =
		new mutable.GrowableBuilder(LinkedList[A]())
	
	override def empty[A]: LinkedList[A] =
		new LinkedList[A]
	
}
