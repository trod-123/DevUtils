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

import android.animation.Animator
import android.content.Context
import android.media.MediaPlayer
import android.os.Vibrator
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.snackbar.Snackbar
import io.trod.devutils.annotations.Untested
import io.trod.devutils.constants.Defaults
import java.io.IOException
import java.util.*

object UiUtils {

    private lateinit var toast: Toast
    private lateinit var snackbar: Snackbar
    private lateinit var snackbarMessage: String

    object EditTextUtils {
        /**
         * Returns all strings from the provided [List] of [EditText] objects, in 1:1 order
         *
         * @param editTexts
         * @return
         */
        @JvmStatic
        fun getStringsFromEditTexts(editTexts: List<EditText>): List<String> {
            val values = ArrayList<String>()
            for (editText in editTexts) {
                values.add(editText.text.toString().trim { it <= ' ' })
            }
            return values
        }
    }

    /**
     * Returns true if device is currently in LTR layout
     *
     * See more at this [Stackoverflow post](https://stackoverflow.com/questions/26549354/android-determine-if-device-is-in-right-to-left-language-layout)
     *
     * @return true if device is in LTR layout
     */
    @JvmStatic
    fun isLeftToRightLayout(): Boolean {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_LTR
    }

    /**
     * Display Toasts at a default duration, ensuring they do not overlap with each other
     *
     * @param context with which to display the toast message
     * @param message to be displayed
     */
    @JvmStatic
    fun showToast(context: Context, message: String) {
        if (this::toast.isInitialized) toast.cancel()
        toast = Toast.makeText(context, message, Defaults.DEFAULT_TOAST_LENGTH)
        toast.show()
    }

    /**
     * Display Snackbars, ensuring they do not overlap with each other if the message is the same
     */
    @JvmStatic
    fun showSnackbar(
        view: View, message: String, shownLength: Int = Defaults.DEFAULT_SNACKBAR_LENGTH,
        actionMessage: CharSequence? = null, actionListener: View.OnClickListener? = null,
        callback: Snackbar.Callback? = null
    ) {
        if (!this::snackbar.isInitialized ||
            (this::snackbar.isInitialized && (!snackbar.isShown || snackbarMessage != message))
        ) {
            snackbarMessage = message
            snackbar = Snackbar.make(
                view,
                snackbarMessage, shownLength
            )
            if (actionMessage != null && actionListener != null) snackbar.setAction(actionMessage, actionListener)
            callback?.let { snackbar.addCallback(callback) }
            snackbar.show()
        }
    }

    /**
     * Vibrates device for a specified amount of time, in millis.
     *
     * See more at this [Stackoverflow link](https://stackoverflow.com/questions/9079632/android-vibrate-on-touch)
     *
     * @param context used to access the system's vibrator service
     * @param duration in milliseconds to vibrate device
     */
    @JvmStatic
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    fun vibrate(context: Context, duration: Long) {
        val vb: Vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vb.vibrate(duration)
    }

    /**
     * Plays a beep sound
     *
     * https://stackoverflow.com/questions/3289038/play-audio-file-from-the-assets-directory
     *
     * @param context
     * @throws IOException
     */
    @JvmStatic
    @Untested
    @Throws(IOException::class)
    fun playBeep(context: Context, assetFileName: String) {
        val afd = context.assets.openFd(assetFileName)
        val mp = MediaPlayer()
        mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()

        mp.prepare()
        mp.setVolume(0.25f, 0.25f)
        mp.setOnCompletionListener { mp -> mp.release() }
        mp.start()
    }

    /**
     * Shows a popup menu
     *
     * See more at this [Stackoverflow link](https://stackoverflow.com/questions/30417223/how-to-add-menu-button-without-action-bar)
     */
    @JvmStatic
    fun showMenuPopup(context: Context, view: View, menuResId: Int, listener: PopupMenu.OnMenuItemClickListener) {
        val popup = PopupMenu(context, view)
        popup.setOnMenuItemClickListener(listener)
        val inflater = popup.menuInflater
        inflater.inflate(menuResId, popup.menu)
        popup.show()
    }

    /**
     * Enables touch responses
     *
     * @param window
     * @param enable
     */
    @JvmStatic
    fun enableTouchResponse(window: Window, enable: Boolean) {
        if (!enable) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
    }

    /**
     * Shows or hides views smoothly with an alpha transition.
     *
     * If button, hide completely to negate potential touch events while it's not showing
     *
     * @param show true to show the view
     */
    @JvmStatic
    fun showView(
        view: View, show: Boolean, isButton: Boolean,
        animate: Boolean
    ) {
        view.animate()
            .alpha(if (show) 1f else 0f)
            .setDuration((if (animate) Defaults.DURATION_VISIBILITY_ANIMATION else 0).toLong())
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    if (show)
                        view.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    if (!show && isButton)
                        view.visibility = View.GONE
                }

                override fun onAnimationCancel(animation: Animator) {
                    if (!show && isButton)
                        view.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animator) {
                    if (show)
                        view.visibility = View.VISIBLE
                }
            })
    }

}