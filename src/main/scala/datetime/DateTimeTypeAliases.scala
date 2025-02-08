package cc.sukazyo.std
package datetime

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneOffset}

/** Contains the type alias for common used UTC date-time description in number format.
  * 
  * For some cases that need to use a number to describe the times, but only using numbers may
  * be confusing of the unit of times.
  * This types provides a way that can describe time in number format, but also provide the unit
  * or usage information in the type tag.
  * 
  * The types are:
  * 
  * - [[EpochMillis]]: Milliseconds, since 00:00:00 UTC on Thursday, 1 January 1970.
  *   In most programs, saves as a [[Long]] number.
  * - [[EpochSeconds]]: Seconds, since 00:00:00 UTC on Thursday, 1 January 1970.
  *   In most programs, saves as a [[Int]] number.
  * - [[DurationMillis]]: Milliseconds for time duration/interval.
  *   Mostly used to compute one [[EpochMillis]] minus another [[EpochMillis]], so this type
  *   is also a [[Long]] number.
  * - [[EpochDays]]: Days, since 00:00:00 UTC on Thursday, 1 January 1970.
  *   This unit seems very rarely used, only here for some special cases.
  *   In this library, saves as a [[Short]] number.
  * 
  * All of these aren't recommended to use.
  * If possible, the [[java.time.ZonedDateTime]] and [[java.time.Duration]] is more recommended,
  * for those are more standard and object-oriented.
  * But if you really need to use something like [[Long]] number to describe times (like
  * [[System.currentTimeMillis]], which is also a standard library), these types may help you.
  * 
  * @since 0.2.0
  */
object DateTimeTypeAliases {
	
	/** The UNIX Epoch Time in milliseconds.
	  *
	  * aka. Milliseconds since 00:00:00 UTC on Thursday, 1 January 1970.
	  *
	  * @since 0.2.0
	  */
	type EpochMillis = Long
	
	/** Some utilities for [[EpochMillis]], converts between different time formats.
	  * @since 0.2.0
	  */
	object EpochMillis:
		
		/** convert a localtime with timezone to epoch milliseconds
		  *
		  * @since 0.2.0
		  *
		  * @param time the local time in that timezone, should be formatted
		  *             in [[DateTimeFormatter.ISO_DATE_TIME]]
		  * @param zone timezone of the localtime.
		  *             cannot be "UTC" or "GMT" (use "Z" instead)
		  * @return the epoch millisecond the local time means.
		  */
		def apply (time: String, zone: String): EpochMillis = {
			val formatter = DateTimeFormatter.ISO_DATE_TIME
			val innerTime = LocalDateTime.parse(time, formatter).asInstanceOf[LocalDateTime]
			val instant = innerTime.toInstant(ZoneOffset `of` zone).asInstanceOf[Instant]
			instant.toEpochMilli
		}
		/** Same with [[apply(String, String)]], but accept a tuple instead of two params.
		  * 
		  * @since 0.2.0
		  * 
		  * @param time_zone a tuple of time string and zone string.
		  * @return the epoch millisecond the local time means.
		  */
		def apply (time_zone: (String, String)): EpochMillis =
			time_zone match
				case (time, zone) => apply(time, zone)
		
		/** Convert from [[EpochSeconds]].
		  *
		  * Due to the missing accuracy, the converted EpochMillis will
		  * be always in 0 ms aligned.
		  *
		  * @since 0.2.0
		  */
		infix def fromSeconds (epochSeconds: EpochSeconds): EpochMillis =
			epochSeconds.longValue * 1000L
	
	/** The UNIX Epoch Time in seconds.
	  *
	  * aka. Seconds since 00:00:00 UTC on Thursday, 1 January 1970.
	  *
	  * Normally is the `epochSeconds = (epochMillis / 1000)`
	  *
	  * Notice that, currently, it stores using [[Int]] (also the implementation
	  * method of Telegram), which will only store times before 2038-01-19 03:14:07.
	  *
	  * @since 0.2.0
	  */
	type EpochSeconds = Int
	
	/** The UNIX Epoch Time in day.
	  *
	  * aka. days since 00:00:00 UTC on Thursday, 1 January 1970.
	  *
	  * Normally is the epochDays = (epochMillis / 1000 / 60 / 60 / 24)
	  *
	  * Notice that, currently, it stores using [[Short]] (also the implementation
	  * method of Telegram), which will only store times before 2059-09-18.
	  *
	  * @since 0.2.0
	  */
	type EpochDays = Short
	
	object EpochDays:
		/** Convert a [[EpochMillis]] to [[EpochDays]]. Will be loss of precision.
		  *
		  * @since 0.2.0
		  */
		infix def fromMillis (epochMillis: EpochMillis): EpochDays =
			(epochMillis / (1000 * 60 * 60 * 24)).toShort
	
	/** Time duration/interval in milliseconds.
	  *
	  * @since 0.2.0
	  */
	type DurationMillis = Long
	
}
