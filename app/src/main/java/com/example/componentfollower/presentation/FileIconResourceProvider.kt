package com.example.componentfollower.presentation

import androidx.annotation.DrawableRes
import com.example.componentfollower.R
import com.example.componentfollower.domain.model.FileToShow

class FileIconResourceProvider {

    @DrawableRes
    fun provideByExtension(file: FileToShow): Int? {
        if (file.isDirectory) {
            return R.drawable.baseline_folder_24
        }

        return when (file.extension) {
            "ans", "ascii", "log", "rtf", "txt", "wpd" -> {
                R.drawable.text_file
            }

            "3g2", "3gp", "asf", "avi", "mkv", "mov", "mpeg", "mpg", "wmv", "3gpp", "h261",
            "h263", "h264", "jpgv", "jpm", "jpgm", "mp4", "mp4v", "mpg4", "mpe", "m1v", "m2v",
            "ogv", "qt", "m4u", "webm", "f4v", "fli", "m4v", "mk3d", "movie" -> {
                R.drawable.video_file
            }

            "jpg", "jpeg", "tga", "tif", "tiff", "bmp", "gif", "png" -> {
                R.drawable.image_file
            }

            "aif", "aiff", "wav", "flac", "iff", "m4a",
            "wma", "oga", "ogg", "mp3", "3ga", "opus", "weba" -> {
                R.drawable.audio_file
            }

            else -> {
                null
            }
        }
    }
}