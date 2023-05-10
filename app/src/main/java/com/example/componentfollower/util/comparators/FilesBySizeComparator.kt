package com.example.componentfollower.util.comparators

import java.io.File

class FilesBySizeComparator : Comparator<File> {

    override fun compare(file1: File, file2: File): Int {
        val file1Length = if (file1.isDirectory) {
            file1.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
        } else {
            file1.length()
        }

        val file2Length = if (file2.isDirectory) {
            file2.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
        } else {
            file2.length()
        }

        return file1Length.compareTo(file2Length)
    }
}