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

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import io.trod.devutils.constants.Defaults
import timber.log.Timber
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object StorageUtils {

    object BitmapStorageUtils {
        /**
         * Saves a [Bitmap] into the app's internal storage
         *
         * See more at this [Stackoverflow post](https://stackoverflow.com/questions/17674634/saving-and-reading-bitmaps-images-from-internal-memory-in-android)
         *
         * @param bitmap to be saved into the device's internal directory
         * @param filename
         * @param context
         * @return [String] representing the filepath of the stored file, which can safely be used in Glide
         * @throws IOException
         */
        @Throws(IOException::class)
        fun saveBitmapToInternalStorage(bitmap: Bitmap, filename: String, context: Context): String {
            val cw = ContextWrapper(context)
            val path = getBitmapSavingFilePath(cw, filename)
            val fos: FileOutputStream
            fos = FileOutputStream(path)
            // Use the compress method on the Bitmap object to write image to fos
            bitmap.compress(Bitmap.CompressFormat.JPEG, Defaults.BITMAP_SAVING_QUALITY, fos)
            fos.close()
            return path.absolutePath
        }

        /**
         * Generates a file to be used for outputting a bitmap, with the following filename syntax:
         * `currentTime_filename.jpg`
         *
         * Call [File.getAbsolutePath] on the result to get a [String] that represents the filepath
         * of the file, which can safely be used in Glide
         *
         * See more at this [Stackoverflow post](https://developer.android.com/training/camera/photobasics)
         *
         * @param context
         * @param filename
         * @return
         */
        @Throws(IOException::class)
        fun getBitmapSavingFilePath(context: Context, filename: String): File {
            var filename = filename
            filename = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + "_" + filename
            // filepath: Android/data/com.zn.expirytracker.free.debug/files/Pictures
            // (also defined in file_paths.xml)
            val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(filename, ".jpg", directory)
        }


        /**
         * Deletes the app's bitmap directory and all of its contents
         *
         * @param context used to access the app's file directory
         */
        fun deleteBitmapDirectory(context: Context) {
            val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (directory != null) {
                deleteRecursive(directory)
            } else {
                Timber.e("There was an issue deleting the bitmap directory")
            }
        }

        /**
         * Deletes a single file, assuming it is in app's internal data
         *
         * @param uriToDelete
         * @return `true` if the file is successfully deleted, false otherwise
         */
        fun deleteBitmapFromInternalStorage(uriToDelete: Uri, tag: String): Boolean {
            val file = File(uriToDelete.path)
            val success = file.delete()
            if (success) {
                Timber.d("$tag: local image delete success. uri: $uriToDelete")
            } else {
                Timber.d("$tag: local image delete failed. uri: $uriToDelete")
            }
            return success
        }


        /**
         * Returns a cached path for a Uri obtained through `Intent.ACTION_PICK` by first saving a copy of the file
         * to a cache, whose path is then returned so the path can be fed into [File()]
         *
         * Function originally used for getting a path from Uri from the Google Photos app. See more at this
         * [Stackoverflow post](https://stackoverflow.com/questions/43500164/getting-path-from-uri-from-google-photos-app)
         *
         * @param context
         * @param uri obtained through `Intent.ACTION_PICK`
         * @return path of the cached copy of the original file
         */
        fun getImagePathFromInputStreamUri(context: Context, uri: Uri, filename: String = "tempFile.jpg"): String? {
            var inputStream: InputStream? = null
            var filePath: String? = null

            if (uri.authority != null) {
                try {
                    inputStream = context.contentResolver.openInputStream(uri) // context needed
                    val photoFile =
                        writeDataToTempFile(context, inputStream, filename)
                    filePath = photoFile!!.path
                } catch (e: FileNotFoundException) {
                    Timber.e(e, "Error getting image path from input stream. uri: %s", uri)
                } catch (e: IOException) {
                    Timber.e(e, "Error getting image path from input stream. uri: %s", uri)
                } finally {
                    try {
                        inputStream?.close()
                    } catch (e: IOException) {
                        Timber.e(e)
                    }

                }
            }
            return filePath
        }
    }

    /**
     * Recursively deletes all files and folders
     *
     * See more at this [Stackoverflow post](https://stackoverflow.com/questions/4943629/how-to-delete-a-whole-folder-and-content)
     *
     * @param fileOrDirectory
     * @return `true` if the deletion was successful, `false` otherwise
     */
    private fun deleteRecursive(fileOrDirectory: File): Boolean {
        if (fileOrDirectory.isDirectory) {
            for (child in fileOrDirectory.listFiles()) {
                deleteRecursive(child)
            }
        }
        return fileOrDirectory.delete()
    }

    /**
     * Copies a file
     *
     * See more at this [Stackoverflow post](https://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android)
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    @Throws(IOException::class)
    fun copyFile(src: File, dst: File) {
        FileInputStream(src).use { `in` ->
            FileOutputStream(dst).use { out ->
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while ((`in`.read(buf)) > 0) {
                    len = `in`.read(buf)
                    out.write(buf, 0, len)
                }
            }
        }
    }

    /**
     * Writes data to a temp file
     *
     * Function originally used for getting a path from Uri from the Google Photos app. See more at this
     * [Stackoverflow post](https://stackoverflow.com/questions/43500164/getting-path-from-uri-from-google-photos-app)
     *
     * @param context for accessing the app's storage on device
     * @param inputStream containing data to be written
     * @return the temp file
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeDataToTempFile(context: Context, inputStream: InputStream?, filename: String): File? {
        var targetFile: File? = null

        if (inputStream != null) {
            var read: Int
            val buffer = ByteArray(8 * 1024)

            targetFile = makeEmptyTempFile(context, filename)
            val outputStream = FileOutputStream(targetFile)

            while ((inputStream.read(buffer)) != -1) {
                read = inputStream.read(buffer)
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()

            try {
                outputStream.close()
            } catch (e: IOException) {
                Timber.e(e)
            }

        }
        return targetFile
    }

    /**
     * Creates an empty file in the app's cache directory
     *
     * Function originally used for getting a path from Uri from the Google Photos app. See more at this
     * [Stackoverflow post](https://stackoverflow.com/questions/43500164/getting-path-from-uri-from-google-photos-app)
     *
     * @param context
     * @param filename of the temp file, including the extension (e.g. `tempFile.jpg`)
     * @return the empty file
     */
    private fun makeEmptyTempFile(context: Context, filename: String): File {
        return File(context.externalCacheDir, filename) // context needed
    }

    /**
     * Returns a Uri for a given path
     *
     * This can be used to allow local image uris to be uploaded to Firebase Storage
     *
     * @param path (e.g. `/storage/emulated/0/Android/data/`)
     * @return the uri
     */
    fun getUriFromPath(path: String): Uri {
        return Uri.fromFile(File(path))
    }
}