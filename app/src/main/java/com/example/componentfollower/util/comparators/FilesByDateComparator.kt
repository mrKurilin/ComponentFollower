package com.example.componentfollower.util.comparators

import java.io.File

class FilesByDateComparator : Comparator<File> {

    override fun compare(file1: File, file2: File): Int {
        return file1.lastModified().compareTo(file2.lastModified())
    }
}