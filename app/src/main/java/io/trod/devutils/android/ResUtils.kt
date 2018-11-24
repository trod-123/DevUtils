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

import android.content.res.Resources
import android.util.TypedValue

/**
 * Utils for accessing resource values
 */
object ResUtils {

    /**
     * Utility method for getting a float value from resources
     *
     * See more at this [Stackoverflow link](]https://stackoverflow.com/questions/3282390/add-floating-point-value-to-android-resources-values)
     *
     * @param resources containing the float resource
     * @param resId of the float resource to fetch
     * @return [Float] value fetched from [Resources]
     */
    @JvmStatic
    fun getFloatFromResources(resources: Resources, resId: Int): Float {
        val outValue = TypedValue()
        resources.getValue(resId, outValue, true)
        return outValue.float
    }
}