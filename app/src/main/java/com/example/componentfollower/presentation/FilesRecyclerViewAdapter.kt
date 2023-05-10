package com.example.componentfollower.presentation

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.componentfollower.domain.model.FileToShow

class FilesRecyclerViewAdapter(
    private val openFile: (FileToShow) -> Unit,
    private val fileIconResourceProvider: FileIconResourceProvider,
) : RecyclerView.Adapter<FileViewHolder>() {

    private var files = listOf<FileToShow>()

    override fun getItemCount(): Int = files.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FileViewHolder {
        val fileViewHolder = FileViewHolder(parent)

        fileViewHolder.binding.root.setOnClickListener {
            val file = files[fileViewHolder.adapterPosition]
            openFile(file)
        }

        return fileViewHolder
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.bind(
            file,
            fileIconResourceProvider.provideByExtension(file)
        )
    }

    fun setItems(files: List<FileToShow>) {
        this.files = files
        notifyDataSetChanged()
    }
}