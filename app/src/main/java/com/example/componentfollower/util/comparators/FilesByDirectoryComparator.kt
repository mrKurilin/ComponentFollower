package com.example.componentfollower.util.comparators

import java.io.File

class FilesByDirectoryComparator : Comparator<File> {

    override fun compare(file1: File, file2: File): Int {
        return if (file1.isDirectory && file2.isDirectory)
            0
        else if (file1.isDirectory)
            -1
        else
            1
    }
}
