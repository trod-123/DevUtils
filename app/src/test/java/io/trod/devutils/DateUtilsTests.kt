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

package io.trod.devutils

import io.trod.devutils.java.DateUtils
import org.joda.time.DateTime
import org.joda.time.DateTimeConstants
import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DateUtilsTests {

    /**
     * Assumes [DateUtils.getFirstDayOfWeekDateFromStartDate] is correct!
     */
    @Test
    fun testParseDateStringIntoDateTime() {
        // region cases
        val DATE_TIME = DateTime().withTimeAtStartOfDay()

        val TEST_CASES = arrayOf(
            "Today",
            "Tomorrow",
            "October 14th",
            "October 14th 2019",
            "October 14",
            "October 14 2019",
            "1st",
            "2nd",
            "3rd",
            "4th",
            "20th",
            "Monday",
            "Tuesday",
            "Wednesday",
            "Thursday",
            "Friday",
            "Saturday",
            "Sunday",
            "1 day",
            "2 days",
            "10 days",
            "15 days",
            "One day",
            "Two days",
            "Ten days",
            "Fifteen days",
            "1 week",
            "2 weeks",
            "10 weeks",
            "15 weeks",
            "One week",
            "Two weeks",
            "Ten weeks",
            "Fifteen weeks",
            "1.5 weeks",
            "One point five weeks",
            "1 and a half weeks",
            "One and a half weeks",
            "Half a week",
            "1 and a 1/2 weeks",
            "One and a 1/2 weeks",
            "1/2 a week"
        )
        val TEST_CASE_PREFIXES = arrayOf("", "Expires on ", "On ", "In ", "The ", "On the ", "In the ")
        val TEST_CASE_EXPECTED = arrayOf(
            DATE_TIME,
            DATE_TIME.plusDays(1),
            DateTime(DATE_TIME.year, 10, 14, 0, 0),
            DateTime(2019, 10, 14, 0, 0),
            DateTime(DATE_TIME.year, 10, 14, 0, 0),
            DateTime(2019, 10, 14, 0, 0),
            DATE_TIME.withDayOfMonth(1),
            DATE_TIME.withDayOfMonth(2),
            DATE_TIME.withDayOfMonth(3),
            DATE_TIME.withDayOfMonth(4),
            DATE_TIME.withDayOfMonth(20),
            DateUtils.getFirstDayOfWeekDateFromStartDate(
                DateTimeConstants.MONDAY, DATE_TIME.millis
            ),
            DateUtils.getFirstDayOfWeekDateFromStartDate(
                DateTimeConstants.TUESDAY, DATE_TIME.millis
            ),
            DateUtils.getFirstDayOfWeekDateFromStartDate(
                DateTimeConstants.WEDNESDAY, DATE_TIME.millis
            ),
            DateUtils.getFirstDayOfWeekDateFromStartDate(
                DateTimeConstants.THURSDAY, DATE_TIME.millis
            ),
            DateUtils.getFirstDayOfWeekDateFromStartDate(
                DateTimeConstants.FRIDAY, DATE_TIME.millis
            ),
            DateUtils.getFirstDayOfWeekDateFromStartDate(
                DateTimeConstants.SATURDAY, DATE_TIME.millis
            ),
            DateUtils.getFirstDayOfWeekDateFromStartDate(
                DateTimeConstants.SUNDAY, DATE_TIME.millis
            ),
            DATE_TIME.plusDays(1),
            DATE_TIME.plusDays(2),
            DATE_TIME.plusDays(10),
            DATE_TIME.plusDays(15),
            DATE_TIME.plusDays(1),
            DATE_TIME.plusDays(2),
            DATE_TIME.plusDays(10),
            DATE_TIME.plusDays(15),
            DATE_TIME.plusDays(7),
            DATE_TIME.plusDays(7 * 2),
            DATE_TIME.plusDays(7 * 10),
            DATE_TIME.plusDays(7 * 15),
            DATE_TIME.plusDays(7),
            DATE_TIME.plusDays(7 * 2),
            DATE_TIME.plusDays(7 * 10),
            DATE_TIME.plusDays(7 * 15),
            DATE_TIME.plusDays(11),
            DATE_TIME.plusDays(11),
            DATE_TIME.plusDays(11),
            DATE_TIME.plusDays(11),
            DATE_TIME.plusDays(4),
            DATE_TIME.plusDays(11),
            DATE_TIME.plusDays(11),
            DATE_TIME.plusDays(4)
        )
        // endregion

        val resultsMap = HashMap<String, String>()
        val failedTestCases = ArrayList<String>()

        for (prefix in TEST_CASE_PREFIXES) {
            for (i in TEST_CASES.indices) {
                val testCase = TEST_CASES[i]
                val fullCase = prefix + testCase
                try {
                    val result = DateUtils.parseDateFromString(prefix + testCase)
                    if (result.isEqual(TEST_CASE_EXPECTED[i])) {
                        resultsMap[fullCase] = DateUtils.formatDate(
                            result, null,
                            DateUtils.DateFormatComponents.SHORT_MONTH, DateUtils.DateFormatComponents.FULL_YEAR
                        )
                    } else {
                        failedTestCases.add(fullCase)
                        resultsMap[fullCase] = String.format(
                            "Failed: %s | Expected: %s",
                            DateUtils.formatDate(
                                result, null,
                                DateUtils.DateFormatComponents.SHORT_MONTH, DateUtils.DateFormatComponents.FULL_YEAR
                            ),
                            DateUtils.formatDate(
                                TEST_CASE_EXPECTED[i], null,
                                DateUtils.DateFormatComponents.SHORT_MONTH, DateUtils.DateFormatComponents.FULL_YEAR
                            )
                        )
                    }
                } catch (e: IllegalArgumentException) {
                    failedTestCases.add(fullCase)
                    resultsMap[fullCase] = ""
                }

            }
            for (testCase in resultsMap.keys) {
                println(String.format("%s => %s", testCase, resultsMap[testCase]))
            }
            if (!failedTestCases.isEmpty()) {
                Assert.fail("Failed cases: " + Arrays.toString(failedTestCases.toTypedArray()))
            }
        }
    }
}
