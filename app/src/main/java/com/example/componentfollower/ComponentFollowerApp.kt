package com.example.componentfollower

import android.app.Application
import com.example.componentfollower.data.FileConverter
import com.example.componentfollower.presentation.FileIconResourceProvider
import com.example.componentfollower.util.comparators.FilesByDateComparator
import com.example.componentfollower.util.comparators.FilesByExtensionComparator
import com.example.componentfollower.util.comparators.FilesByNameComparator
import com.example.componentfollower.util.comparators.FilesBySizeComparator

class ComponentFollowerApp : Application() {

    val fileIconResourceProvider = FileIconResourceProvider()

    val fileConverter = FileConverter()

    val filesByDateComparator by lazy { FilesByDateComparator() }
    val filesBySizeComparator by lazy { FilesBySizeComparator() }
    val filesByNameComparator by lazy { FilesByNameComparator() }
    val filesByExtensionComparator by lazy { FilesByExtensionComparator() }
}