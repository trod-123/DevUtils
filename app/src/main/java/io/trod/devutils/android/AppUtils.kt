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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.annotation.RequiresPermission

/**
 * General app-level set of utils
 */
object AppUtils {

    /**
     * Retrieves the version name from the app level build.gradle file
     *
     * See more at this [Stackoverflow link](https://stackoverflow.com/questions/4616095/how-to-get-the-build-version-number-of-your-android-application)
     *
     * @param context used for getting package info containing version name
     * @return [String] representing the version name
     */
    fun getAppVersionName(context: Context): String {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        return pInfo.versionName
    }

    /**
     * Check if this device has a camera
     *
     * @param context used for getting package info for checking system features
     * @return true if this device has a camera */
    fun isCameraOnDevice(context: Context): Boolean {
        return context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
    }

    /**
     * Checks if permission is granted in manifest
     *
     * See more at this [Stackoverflow link](https://stackoverflow.com/questions/8292558/is-there-a-way-to-check-for-manifest-permission-from-code)
     *
     * @param context used for accessing package info for checking permissions
     * @param permission to check. Use [Manifest.permission] to provide the string
     * @return true if permission is granted
     */
    fun isManifestPermissionGranted(context: Context, permission: String): Boolean {
        return context.packageManager.checkPermission(permission, context.packageName) ==
                PackageManager.PERMISSION_GRANTED
    }

    /**
     * Creates a uniformly structured key with the provided `name`, prepending the passed class name.
     *
     * @param klass whose name is used to prepend the key
     * @param name of the key
     * @return the key string prepended with the class name
     */
    fun createStaticKeyString(klass: Class<*>, name: String): String {
        return String.format("%s.%s", klass.simpleName, name)
    }

    /**
     * Checks if user has internet connectivity
     *
     * @param context used to access the connectivity service
     * @return true if user is connected to a network
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = cm.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}