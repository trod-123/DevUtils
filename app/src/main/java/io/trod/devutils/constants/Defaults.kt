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

package io.trod.devutils.constants

import android.widget.Toast
import com.google.android.material.snackbar.Snackbar

object Defaults {

    const val DEFAULT_TOAST_LENGTH = Toast.LENGTH_SHORT
    const val DEFAULT_SNACKBAR_LENGTH = Snackbar.LENGTH_LONG
    const val DURATION_VISIBILITY_ANIMATION: Long = 300 // millis
    const val BITMAP_SAVING_QUALITY = 30 // out of 100
}