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

package io.trod.devutils.android

import android.util.SparseArray
import android.util.SparseIntArray
import io.trod.devutils.annotations.Untested
import java.util.*

/**
 * Utils for numbers
 */
object NumberUtils {

    private val DIGITS = arrayOf(
        "one",
        "two",
        "three",
        "four",
        "five",
        "six",
        "seven",
        "eight",
        "nine"
    )
    private val TENS = arrayOf(
        null,
        "twenty",
        "thirty",
        "forty",
        "fifty",
        "sixty",
        "seventy",
        "eighty",
        "ninety"
    )
    private val TEENS = arrayOf(
        "ten",
        "eleven",
        "twelve",
        "thirteen",
        "fourteen",
        "fifteen",
        "sixteen",
        "seventeen",
        "eighteen",
        "nineteen"
    )
    private val MAGNITUDES = arrayOf("hundred", "thousand", "million", "point")
    private val ZERO = arrayOf("zero", "oh")

    /**
     * Converts a spelled out number into an integer string
     *
     * See more at this [Stackoverflow post](https://stackoverflow.com/questions/4062022/how-to-convert-words-to-a-number)
     *
     * @param input Number written out in English
     * @return [String] expressing the number written out in the decimal numeral system
     */
    @JvmStatic
    fun convertEnglishWordsToDecimal(input: String): String {
        var result = ""
        val decimal = input.split(MAGNITUDES[3].toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val millions = decimal[0].split(MAGNITUDES[2].toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (i in millions.indices) {
            val thousands = millions[i].split(MAGNITUDES[1].toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            for (j in thousands.indices) {
                val triplet = intArrayOf(0, 0, 0)
                val set = StringTokenizer(thousands[j])

                if (set.countTokens() == 1) { //If there is only one token given in triplet
                    val uno = set.nextToken()
                    triplet[0] = 0
                    for (k in DIGITS.indices) {
                        if (uno == DIGITS[k]) {
                            triplet[1] = 0
                            triplet[2] = k + 1
                        }
                        if (uno == TENS[k]) {
                            triplet[1] = k + 1
                            triplet[2] = 0
                        }
                        if (uno == TEENS[k]) {
                            triplet[2] = k + 10
                        }
                    }
                } else if (set.countTokens() == 2) {  //If there are two tokens given in triplet
                    val uno = set.nextToken()
                    val dos = set.nextToken()
                    if (dos == MAGNITUDES[0]) {  //If one of the two tokens is "hundred"
                        for (k in DIGITS.indices) {
                            if (uno == DIGITS[k]) {
                                triplet[0] = k + 1
                                triplet[1] = 0
                                triplet[2] = 0
                            }
                        }
                    } else {
                        triplet[0] = 0
                        for (k in DIGITS.indices) {
                            if (uno == TENS[k]) {
                                triplet[1] = k + 1
                            }
                            if (dos == DIGITS[k]) {
                                triplet[2] = k + 1
                            }
                        }
                    }
                } else if (set.countTokens() == 3) {  //If there are three tokens given in triplet
                    val uno = set.nextToken()
                    val dos = set.nextToken()
                    val tres = set.nextToken()
                    for (k in DIGITS.indices) {
                        if (uno == DIGITS[k]) {
                            triplet[0] = k + 1
                        }
                        if (tres == DIGITS[k]) {
                            triplet[1] = 0
                            triplet[2] = k + 1
                        }
                        if (tres == TENS[k]) {
                            triplet[1] = k + 1
                            triplet[2] = 0
                        }
                    }
                } else if (set.countTokens() == 4) {  //If there are four tokens given in triplet
                    val uno = set.nextToken()
                    val dos = set.nextToken()
                    val tres = set.nextToken()
                    val cuatro = set.nextToken()
                    for (k in DIGITS.indices) {
                        if (uno == DIGITS[k]) {
                            triplet[0] = k + 1
                        }
                        if (cuatro == DIGITS[k]) {
                            triplet[2] = k + 1
                        }
                        if (tres == TENS[k]) {
                            triplet[1] = k + 1
                        }
                    }
                } else {
                    triplet[0] = 0
                    triplet[1] = 0
                    triplet[2] = 0
                }

                result = result + Integer.toString(triplet[0]) + Integer.toString(triplet[1]) +
                        Integer.toString(triplet[2])
            }
        }

        if (decimal.size > 1) {  //The number is a decimal
            val decimalDigits = StringTokenizer(decimal[1])
            result = "$result."
            //            System.out.println(decimalDigits.countTokens() + " decimal digits");
            while (decimalDigits.hasMoreTokens()) {
                val w = decimalDigits.nextToken()
                //                System.out.println(w);

                if (w == ZERO[0] || w == ZERO[1]) {
                    result += "0"
                }
                for (j in DIGITS.indices) {
                    if (w == DIGITS[j]) {
                        result += Integer.toString(j + 1)
                    }
                }

            }
        }

        return result
    }

    private val SUFFIXES = arrayOf("th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th")

    /**
     * Converts an integer into its ordinal representation
     *
     * See more from this [Stackoverflow post](https://stackoverflow.com/questions/6810336/is-there-a-way-in-java-to-convert-an-integer-to-its-ordinal)
     *
     * @param i
     * @return
     */
    @JvmStatic
    @Untested
    fun convertIntToOrdinal(i: Int): String {
        return when (i % 100) {
            11, 12, 13 -> i.toString() + "th"
            else -> i.toString() + SUFFIXES[i % 10]
        }
    }

    const val NO_MAX_SIZE = 0
    const val NO_INDEX_LIMIT = Integer.MAX_VALUE

    private const val NO_MAPPING_FOUND = -65536

    /**
     * Returns the frequencies of each element of a dataset as a [SparseIntArray], where the `key` is the element,
     * and the `value` is that element's frequency in the dataset.
     *
     * - If `fillInGaps` is true, then all gaps in the dataset will be filled in with `0`s.
     * - If `data` is empty AND `fillInGaps` is NOT true, then this returns an empty [SparseIntArray]
     *
     * See more on [SparseArray] at this [Stackoverflow post](https://stackoverflow.com/questions/25560629/sparsearray-vs-hashmap)
     *
     * The original use of this function was to generate data to be displayed in a Chart provided by MPAndroidChart
     *
     * @param data to go through
     * @param fillInGaps `true` to fill in gaps with `0`s. `true` by default
     * @param maxSize Provide to guarantee a size for the array if `fillInGaps == true`. [NO_MAX_SIZE] by default
     * @return
     */
    @JvmStatic
    @Untested
    fun getIntFrequencies(data: IntArray, fillInGaps: Boolean = true, maxSize: Int = NO_MAX_SIZE): SparseIntArray {
        val limit = if (maxSize == NO_MAX_SIZE) data.size else maxSize
        if (data.isNotEmpty()) {
            Arrays.sort(data)
            val array = SparseIntArray()
            if (fillInGaps) {
                for (i in 0 until limit) {
                    // create a key for every element in data, including for those elements that
                    // don't exist. initialize each value to 0
                    array.put(i, 0)
                }
            }
            for (element in data) {
                if (array.get(
                        element,
                        NO_MAPPING_FOUND
                    ) == NO_MAPPING_FOUND
                ) {
                    // Add the key if it doesn't already exist, setting its initial value to 1
                    // Note this won't be called if fillInGaps is true, as all keys will already exist
                    array.put(element, 1)
                } else {
                    // If the key already exists, then add 1 to its value by taking it out then putting
                    // it back in
                    var previousValue = array.get(element)
                    array.put(element, ++previousValue)
                }
            }
            return array
        } else {
            val array = SparseIntArray()
            if (fillInGaps) {
                for (i in 0 until limit) {
                    // create a key for every element in data, including for those elements that
                    // don't exist. initialize each value to 0
                    array.put(i, 0)
                }
            }
            return array
        }
    }

    /**
     * Returns the most frequently occurring number in a [SparseIntArray]
     *
     * @param data to go through
     * @param includeNegativeKeys `true` to include negative values
     * @param sorted `true` if the array is sorted
     * @return most frequently occurring [Int]
     */
    @JvmStatic
    @Untested
    fun getHighestIntFrequency(
        data: SparseIntArray,
        includeNegativeKeys: Boolean = false,
        sorted: Boolean = true
    ): Int {
        var highest = 0
        val startIndex = if (sorted && !includeNegativeKeys) {
            val calculatedIndex = getStartingPositiveIndex(data, NO_INDEX_LIMIT)
            if (calculatedIndex != -1) calculatedIndex else 0
        } else 0
        for (i in startIndex until data.size()) {
            val current = data.get(i)
            if (current > highest) {
                highest = current
            }
        }
        return highest
    }

    /**
     * Gets the index of the first non-negative key. Assumes `data` keys are sorted in increasing order. If all keys
     * are negative, then this returns `-1`
     *
     * @param data sorted in increasing key order
     * @param limit index at which to stop iterating
     * @return first index at which the key is 0 or greater. If there is no such key, then returns `-1`
     */
    @JvmStatic
    @Untested
    private fun getStartingPositiveIndex(data: SparseIntArray, limit: Int): Int {
        var limit = limit
        val entriesSize = data.size()
        var index = 0
        while (index < limit && index < entriesSize - 1) {
            if (data.keyAt(index) >= 0) break
            index++
            if (limit != NO_INDEX_LIMIT)
                limit++ // offset limit since we haven't reached point where key is 0
        }
        return if (index > entriesSize) -1 else index
    }
}