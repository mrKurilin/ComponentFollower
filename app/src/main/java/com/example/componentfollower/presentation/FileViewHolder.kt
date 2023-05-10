package com.example.componentfollower.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.example.componentfollower.R
import com.example.componentfollower.databinding.FileListItemBinding
import com.example.componentfollower.domain.model.FileToShow

class FileViewHolder private constructor(
    val binding: FileListItemBinding,
) : RecyclerView.ViewHolder(binding.root) {

    constructor(parent: ViewGroup) : this(
        FileListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    fun bind(
        file: FileToShow,
        @DrawableRes
        imageResource: Int?,
    ) {
        setFileIcon(imageResource, file.extension)

        binding.fileName.text = file.name

        binding.fileSize.text = itemView.context.getString(
            R.string.file_size_in_mb,
            file.sizeInMb
        )

        binding.fileDate.text = file.lastModifiedString
    }

    private fun setFileIcon(imageResource: Int?, extension: String) {
        if (imageResource == null) {
            binding.commonFileIcon.visibility = View.INVISIBLE
            binding.otherFileIcon.visibility = View.VISIBLE
            binding.otherFileIcon.text = if (extension.length <= 4) {
                extension
            } else {
                "FILE"
            }
        } else {
            binding.commonFileIcon.visibility = View.VISIBLE
            binding.otherFileIcon.visibility = View.INVISIBLE
            binding.commonFileIcon.setImageResource(imageResource)
        }
    }
}