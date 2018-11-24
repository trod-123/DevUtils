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

import java.util.*

/**
 * General utils that do not rely on the Android framework
 */
object JavaUtils {
    /**
     * Returns a random Object from an array
     *
     * @param objects
     * @return
     */
    @JvmStatic
    fun getRandomObjectFromArray(objects: Array<Any>): Any {
        return objects[Random().nextInt(objects.size)]
    }
}