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

/**
 * Utils for Strings
 */
object StringUtils {

    /**
     * Converts a string input into Title Case
     *
     * See more at this [Stackoverflow post](https://stackoverflow.com/questions/1086123/string-conversion-to-title-case)
     *
     * @param input [String] to be converted to Title Case
     * @return [String] converted to Title Case
     */
    fun toTitleCase(input: String): String {
        val titleCase = StringBuilder()
        var nextTitleCase = true

        for (c in input.toCharArray()) {
            var c = c
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c)
                nextTitleCase = false
            }
            titleCase.append(c)
        }
        return titleCase.toString()
    }
}