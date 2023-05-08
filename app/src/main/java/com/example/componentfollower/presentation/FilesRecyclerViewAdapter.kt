package com.example.componentfollower.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.text.SimpleDateFormat

class FilesRecyclerViewAdapter(
    private val simpleDateFormat: SimpleDateFormat,
    private val openFile: (File) -> Unit,
) : RecyclerView.Adapter<FileViewHolder>() {

    private var files = listOf<File>()

    override fun getItemCount(): Int = files.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val fileViewHolder = FileViewHolder(
            parent = parent,
            simpleDateFormat = simpleDateFormat
        )

        fileViewHolder.binding.root.setOnClickListener {
            val file = files[fileViewHolder.adapterPosition]
            openFile(file)
        }

        return fileViewHolder
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        holder.bind(files[position])
    }

    fun setItems(files: List<File>) {
        val diffResult = DiffUtil.calculateDiff(
            DiffUtilCallback(
                this.files,
                files,
            )
        )
        this.files = files
        diffResult.dispatchUpdatesTo(this)
    }
}