package com.example.componentfollower.util.comparators

import java.io.File

class FilesByExtensionComparator : Comparator<File> {

    override fun compare(file1: File, file2: File): Int {
        return file1.extension.compareTo(file2.extension)
    }
}
