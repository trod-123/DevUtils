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

import android.widget.EditText
import io.trod.devutils.annotations.Untested

/**
 * Stores cached contents of [EditText] references to allow checking whether [EditText] contents have changed since
 * this class's initialization
 */
@Untested
class EditTextFormChangedDetector<T : EditText>(
    private var _editTextsReference: List<T>,
    private var _cachedStrings: List<String> = UiUtils.EditTextUtils.getStringsFromEditTexts(_editTextsReference)
) {
    /**
     * Compares the current strings of the [EditText] references stored in the [EditTextFormChangedDetector] instance with its
     * cached strings
     *
     * @return `true` if any [EditText] strings are different
     */
    fun haveFieldsChanged(): Boolean {
        val updatedStrings = UiUtils.EditTextUtils.getStringsFromEditTexts(_editTextsReference)
        for (i in _cachedStrings.indices) {
            val cached = _cachedStrings[i]
            val updated = updatedStrings[i]
            if (cached != updated) {
                return true
            }
        }
        return false
    }

    /**
     * Updates the list of cached strings. Subsequently calling [haveFieldsChanged()] would return `true`
     */
    fun updateCachedFields() {
        _cachedStrings = UiUtils.EditTextUtils.getStringsFromEditTexts(_editTextsReference)
    }
}