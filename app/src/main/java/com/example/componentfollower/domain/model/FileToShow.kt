package com.example.componentfollower.domain.model

data class FileToShow(
    val extension: String,
    val name: String,
    val sizeInMb: Float,
    val lastModifiedString: String,
    val path: String,
    val isDirectory: Boolean
)