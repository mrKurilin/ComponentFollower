package com.example.componentfollower

import android.app.Application
import com.example.componentfollower.data.FileConverter
import com.example.componentfollower.presentation.FileIconResourceProvider

class ComponentFollowerApp : Application() {

    val fileIconResourceProvider = FileIconResourceProvider()

    val fileConverter = FileConverter()
}