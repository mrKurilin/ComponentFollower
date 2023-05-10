package com.example.componentfollower.util.comparators

import java.io.File

class FilesByNameComparator : Comparator<File> {

    override fun compare(file1: File, file2: File): Int {
        return file1.name.compareTo(file2.name)
    }
}