package com.example.componentfollower.presentation

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallback<T>(
    private val oldList: List<T>,
    private val newList: List<T>,
    private val areItemsTheSameProp: (oldItem: T, newItem: T) -> Boolean = { oldItem, newItem ->
        oldItem == newItem
    },
    private val areContentsTheSameProp: (oldItem: T, newItem: T) -> Boolean = { oldItem, newItem ->
        oldItem == newItem
    },
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return areItemsTheSameProp(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return areContentsTheSameProp(oldItem, newItem)
    }
}