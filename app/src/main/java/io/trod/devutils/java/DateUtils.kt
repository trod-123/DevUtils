/*
 * Copyright 2018 Teddy Rodriguez (TROD) at https://github.com/trod-123
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.trod.devutils.java

import io.trod.devutils.android.NumberUtils
import io.trod.devutils.annotations.Untested
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.joda.time.Days
import org.joda.time.format.DateTimeFormatterBuilder
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utils for handling dates. Uses [DateTime]
 */
object DateUtils {

    enum class DateFormatComponents {
        SHORT_DOW, FULL_DOW, SHORT_MONTH, FULL_MONTH, SHORT_YEAR, FULL_YEAR
    }

    /**
     * Formats a date to output in the form `[DOW, ][MONTH] [DAY][, YEAR] `. If formats are not provided, will just
     * return the day of the month
     */
    @JvmStatic
    @Untested
    fun formatDate(
        date: DateTime,
        includeDow: DateFormatComponents? = null,
        includeMonth: DateFormatComponents? = null,
        includeYear: DateFormatComponents? = null
    ): String {
        val builder = DateTimeFormatterBuilder()
        when (includeDow) {
            DateFormatComponents.SHORT_DOW -> builder.appendDayOfWeekShortText().appendLiteral(", ")
            DateFormatComponents.FULL_DOW -> builder.appendDayOfWeekText().appendLiteral(", ")
            null -> { // do nothing
            }
            else -> throw IllegalArgumentException("Bad argument passed for 'includeDow': $includeDow")
        }
        when (includeMonth) {
            DateFormatComponents.SHORT_MONTH -> builder.appendMonthOfYearShortText().appendLiteral(' ')
            DateFormatComponents.FULL_MONTH -> builder.appendMonthOfYearText().appendLiteral(' ')
            null -> { // do nothing
            }
            else -> throw IllegalArgumentException("Bad argument passed for 'includeMonth': $includeMonth")
        }
        builder.appendDayOfMonth(1)
        when (includeYear) {
            DateFormatComponents.SHORT_YEAR -> builder.appendLiteral(", ").appendYear(2, 2)
            DateFormatComponents.FULL_YEAR -> builder.appendLiteral(", ").appendYear(4, 4)
            null -> { // do nothing
            }
            else -> throw IllegalArgumentException("Bad argument passed for 'includeYear': $includeYear")
        }
        return builder.toFormatter().print(date)
    }

    /**
     * Formats a date to output in the form `[DOW, ][MONTH] [DAY][, YEAR] `. If formats are not provided, will just
     * return the day of the month
     */
    @JvmStatic
    @Untested
    fun formatDate(
        date: Long,
        includeDow: DateFormatComponents? = null,
        includeMonth: DateFormatComponents? = null,
        includeYear: DateFormatComponents? = null
    ): String {
        return formatDate(DateTime(date), includeDow, includeMonth, includeYear)
    }

    /**
     * Neutralizes hours, minutes, seconds, and milliseconds into a [DateTime] object. This is used to prevent
     * confounding date comparisons and calculating the number of days between dates
     *
     * @param timeInMillis
     * @return
     */
    @JvmStatic
    fun getDateTimeStartOfDay(timeInMillis: Long): DateTime {
        return DateTime(timeInMillis).withTimeAtStartOfDay()
    }

    /**
     * Neutralizes hours, minutes, seconds, and milliseconds. This is used to prevent confounding date comparisons
     * and calculating the number of days between dates
     *
     * @param timeInMillis
     * @return
     */
    @JvmStatic
    fun getTimeInMillisStartOfDay(timeInMillis: Long): Long {
        return DateTime(timeInMillis).withTimeAtStartOfDay().millis
    }

    /**
     * Returns the number of days between two dates
     *
     * @param start
     * @param end
     * @return
     */
    @JvmStatic
    fun getNumDaysBetweenDates(start: DateTime, end: DateTime): Int {
        return Days.daysBetween(start, end).days
    }

    /**
     * Returns the number of days between 2 dates, with dates provided in `MILLISECONDS`.
     * Note the dates need not be the current date time at the start of the day.
     *
     *
     * Assumes the second date is equal to or greater than the first
     *
     * @param dateInMillis1
     * @param dateInMillis2
     * @return
     */
    @JvmStatic
    fun getNumDaysBetweenDates(dateInMillis1: Long, dateInMillis2: Long): Int {
        val current = getDateTimeStartOfDay(dateInMillis1)
        val compared = getDateTimeStartOfDay(dateInMillis2)

        return getNumDaysBetweenDates(current, compared)
    }

    /**
     * Gets the date in millis after the number of `plusDays` provided
     *
     * @param currentDateTimeStartOfDay
     * @param plusDays
     * @return
     */
    @JvmStatic
    private fun getDateAfterNumDays(currentDateTimeStartOfDay: DateTime, plusDays: Int): Long {
        return currentDateTimeStartOfDay.plusDays(plusDays).millis
    }

    /**
     * Returns `true` if the first date is equal to or greater than the second date
     *
     * @param firstDateInMillis
     * @param secondDateInMillis
     * @return
     */
    @JvmStatic
    fun compareTwoDates(firstDateInMillis: Long, secondDateInMillis: Long): Boolean {
        return firstDateInMillis >= secondDateInMillis
    }

    /**
     * Gets the date in millis after the number of `plusDays` provided
     *
     * @param baseDateStartOfDay
     * @param plusDays
     * @return
     */
    @JvmStatic
    fun getDateAfterNumDays(baseDateStartOfDay: Long, plusDays: Int): Long {
        return getDateAfterNumDays(DateTime(baseDateStartOfDay), plusDays)
    }

    /**
     * Converts a date string into a [DateTime] object.
     *
     * @param dateString
     * @return
     * @throws IllegalArgumentException failed to parse `dateString`
     */
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun parseDateFromString(dateString: String): DateTime {
        var dateString = dateString
        Timber.d("DateUtils/Parsing date: %s", dateString)
        val today = DateTime().withTimeAtStartOfDay()
        val fakeMutableDateTime = DateTime() // for just setting specific fields

        // (0) Clean up date string
        dateString = dateString.toLowerCase()
        val expires = "expires "
        val on = "on "
        val `in` = "in "
        val the = "the "
        if (dateString.startsWith(expires)) {
            dateString = dateString.substring(expires.length)
        }
        if (dateString.startsWith(on)) {
            dateString = dateString.substring(on.length)
        }
        if (dateString.startsWith(`in`)) {
            dateString = dateString.substring(`in`.length)
        }
        if (dateString.startsWith(the)) {
            dateString = dateString.substring(the.length)
        }

        // (1) Try parsing provided count (days or weeks)

        // days

        if (dateString.matches("\\d+ days?".toRegex())) {
            // Arabic number
            val num = dateString.substring(0, dateString.indexOf("day"))
            return today.plusDays(Integer.parseInt(num.trim { it <= ' ' }))
        }
        if (dateString.matches("\\w+ days?".toRegex())) {
            // Worded number
            val num = dateString.substring(0, dateString.indexOf("day"))
            return today.plusDays(Integer.parseInt(NumberUtils.convertEnglishWordsToDecimal(num.trim { it <= ' ' })))
        }

        // weeks (with fractions)

        if (dateString.matches("\\d+ weeks?".toRegex()) ||
            dateString.matches("\\d+.\\d+ weeks?".toRegex()) ||
            dateString.matches("\\d+ and a half weeks?".toRegex()) ||
            dateString.matches("\\d+ and a 1/2 weeks?".toRegex())
        ) {
            var num = dateString.substring(0, dateString.indexOf("week"))
            if (num.contains("and a half") || num.contains("and a 1/2")) {
                num = num.substring(0, num.indexOf("and a"))
                val weeks = Integer.parseInt(num.trim { it <= ' ' }) * 7
                return today.plusDays(Math.ceil(weeks + 3.5).toInt())
            } else {
                return today.plusDays(Math.ceil(java.lang.Double.parseDouble(num.trim { it <= ' ' }) * 7).toInt())
            }
        }
        if (dateString.matches("\\w+ weeks?".toRegex()) ||
            dateString.matches("\\w+ point \\w+ weeks?".toRegex()) ||
            dateString.matches("\\w+ and a half weeks?".toRegex()) ||
            dateString.matches("\\w+ and a 1/2 weeks?".toRegex())
        ) {
            var num = dateString.substring(0, dateString.indexOf("week"))
            if (num.contains("and a half") || num.contains("and a 1/2")) {
                num = num.substring(0, num.indexOf("and a"))
                val weeks = Integer.parseInt(NumberUtils.convertEnglishWordsToDecimal(num.trim { it <= ' ' })) * 7
                return today.plusDays(Math.ceil(weeks + 3.5).toInt())
            }
            return today.plusDays(
                Math.ceil(
                    java.lang.Double.parseDouble(NumberUtils.convertEnglishWordsToDecimal(num.trim { it <= ' ' })) * 7
                ).toInt()
            )
        }
        if (dateString == "half a week" || dateString == "1/2 a week") {
            return today.plusDays(4)
        }

        // (2) Try converting date from date format

        // https://stackoverflow.com/questions/9945072/convert-string-to-date-in-java
        val dateFormats = arrayOfNulls<SimpleDateFormat>(4)
        dateFormats[0] = SimpleDateFormat("MMMM dd yyyy")
        dateFormats[1] = SimpleDateFormat("MMMM dd")
        dateFormats[2] = SimpleDateFormat("MMM dd")
        dateFormats[3] = SimpleDateFormat("dd")

        var date: Date
        // https://stackoverflow.com/questions/13239972/how-do-you-implement-a-re-try-catch
        var count = 0
        while (true) {
            try {
                // https://stackoverflow.com/questions/28514346/parsing-a-date-s-ordinal-indicator-st-nd-rd-th-in-a-date-time-string/28514476
                date = dateFormats[count]!!.parse(
                    dateString
                        .replace("(?<=\\d)(st|nd|rd|th)".toRegex(), "")
                )
                if (count != 0)
                    date.year = Date().year // set current year if not provided
                if (count == 3)
                    date.month = today.monthOfYear - 1 // set current month if not provided
                return DateTime(date.time)
            } catch (e: ParseException) {
                if (++count == dateFormats.size) {
                    Timber.d(e, "DateUtils/The date string did not fall in any of the date formats provided")
                    break
                }
            }

        }

        // (3) Try converting from date nouns
        if (dateString == "today") {
            return today
        } else if (dateString == "tomorrow") {
            return today.plusDays(1)
        } else if (dateString == "yesterday") {
            return today.minusDays(1)
        }

        // (4) Try converting from day of week
        val dows = intArrayOf(
            DateTimeConstants.MONDAY,
            DateTimeConstants.TUESDAY,
            DateTimeConstants.WEDNESDAY,
            DateTimeConstants.THURSDAY,
            DateTimeConstants.FRIDAY,
            DateTimeConstants.SATURDAY,
            DateTimeConstants.SUNDAY
        )
        for (dow in dows) {
            if (dateString == fakeMutableDateTime.withDayOfWeek(dow).dayOfWeek().asText
                    .toLowerCase()
            ) {
                // Get the first DOW from the current day
                return getFirstDayOfWeekDateFromStartDate(
                    dow,
                    System.currentTimeMillis()
                )
            }
        }

        // Failed to parse date after trying all cases, so just throw an exception
        throw IllegalArgumentException("DateUtils/Failed to parse date string: $dateString")
    }

    /**
     * Gets the first occurrence of `dow` from `startDate`
     *
     * @param dow       Must be a value between 1 and 7, used by [DateTimeConstants]
     * @param startDate The base date, from which we start searching for the first instance of
     * the `dow`
     * @return
     */
    @JvmStatic
    fun getFirstDayOfWeekDateFromStartDate(dow: Int, startDate: Long): DateTime {
        val start = DateTime(startDate).withTimeAtStartOfDay()
        val startDow = start.dayOfWeek
        val diffDow = dow - startDow
        return start.plusDays(if (diffDow <= 0) diffDow + 7 else diffDow) // offset by week if negative
    }
}