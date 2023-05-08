package com.example.componentfollower.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.componentfollower.R
import com.example.componentfollower.databinding.FileListItemBinding
import com.example.componentfollower.doAsync
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class FileViewHolder private constructor(
    val binding: FileListItemBinding,
    private val simpleDateFormat: SimpleDateFormat
) : RecyclerView.ViewHolder(binding.root) {

    constructor(parent: ViewGroup, simpleDateFormat: SimpleDateFormat) : this(
        FileListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ),
        simpleDateFormat
    )

    fun bind(file: File) {
        setImageResource(file)

        binding.fileName.text = file.name

        binding.fileSize.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.VISIBLE

        binding.fileSize.doAsync({
            if (file.isDirectory) {
                itemView.context.getString(
                    R.string.file_size_in_mb,
                    file.walkTopDown().filter { it.isFile }.map { it.length() }.sum()
                        .toFloat() / 1024 / 1024
                )

            } else {
                itemView.context.getString(
                    R.string.file_size_in_mb,
                    file.length().toFloat() / 1024 / 1024
                )
            }
        }) {
            text = it
            visibility = View.VISIBLE
            binding.progressBar.visibility = View.INVISIBLE
        }

        binding.fileDate.text = simpleDateFormat.format(Date(file.lastModified()))
    }

    private fun setImageResource(file: File) {
        if (file.isDirectory) {
            binding.commonFileIcon.setImageResource(R.drawable.baseline_folder_24)
            binding.commonFileIcon.visibility = View.VISIBLE
            binding.otherFileIcon.visibility = View.INVISIBLE
            return
        }

        binding.commonFileIcon.visibility = View.VISIBLE
        binding.otherFileIcon.visibility = View.INVISIBLE

        when (file.extension) {
            "ans", "ascii", "log", "rtf", "txt", "wpd" -> {
                binding.commonFileIcon.setImageResource(R.drawable.text_file)
            }

            "3g2", "3gp", "asf", "avi", "mkv", "mov", "mpeg", "mpg", "wmv", "3gpp", "h261",
            "h263", "h264", "jpgv", "jpm", "jpgm", "mp4", "mp4v", "mpg4", "mpe", "m1v", "m2v",
            "ogv", "qt", "m4u", "webm", "f4v", "fli", "m4v", "mk3d", "movie" -> {
                binding.commonFileIcon.setImageResource(R.drawable.video_file)
            }

            "jpg", "jpeg", "tga", "tif", "tiff", "bmp", "gif", "png" -> {
                binding.commonFileIcon.setImageResource(R.drawable.image_file)
            }

            "aif", "aiff", "wav", "flac", "iff", "m4a",
            "wma", "oga", "ogg", "mp3", "3ga", "opus", "weba" -> {
                binding.commonFileIcon.setImageResource(R.drawable.audio_file)
            }

            else -> {
                binding.commonFileIcon.visibility = View.INVISIBLE
                binding.otherFileIcon.visibility = View.VISIBLE
                binding.otherFileIcon.text = if (file.extension.length <= 4) {
                    file.extension
                } else {
                    "FILE"
                }
            }
        }
    }
}