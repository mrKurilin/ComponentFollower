package com.example.componentfollower.data

import com.example.componentfollower.domain.model.FileToShow
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileConverter {

    private val simpleDateFormat = SimpleDateFormat(
        "dd.MM.yyyy HH:mm:ss",
        Locale.getDefault()
    )

    fun convertToFileToShow(file: File): FileToShow {
        val sizeInMb = if (file.isFile) {
            file.length().toFloat() / 1024 / 1024
        } else {
            file.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
                .toFloat() / 1024 / 1024
        }

        val lastModifiedString = simpleDateFormat.format(Date(file.lastModified()))


        return FileToShow(
            extension = file.extension,
            name = file.name,
            sizeInMb = sizeInMb,
            lastModifiedString = lastModifiedString,
            path = file.path,
            isDirectory = file.isDirectory
        )
    }
}