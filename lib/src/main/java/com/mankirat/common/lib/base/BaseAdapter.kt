@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.mankirat.common.lib.base

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<model : Any, viewBinding : ViewBinding>(baseDiffUtil: BaseDiffCallback<model>, val singleSelection: Boolean = false) : ListAdapter<model, BaseAdapter<model, viewBinding>.BaseViewHolder>(baseDiffUtil) {

    abstract fun setBindViewHolder(binding: viewBinding, model: model, position: Int, context: Context)

    open fun setViewHolder(binding: viewBinding, viewHolder: BaseViewHolder) {}

    abstract fun areContentSame(oldItem: Any, newItem: Any): Boolean

    var clickListener: ((model: model, position: Int) -> Unit)? = null
    var selectedPos: Int = 0
        set(value) {
            val oldPos = field
            field = value
            notifyItemChanged(oldPos)
            notifyItemChanged(value)
        }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val model = getItem(position)
        val binding = holder.binding

        binding.root.setOnClickListener {
            clickListener?.invoke(model, position)
            if (singleSelection) selectedPos = position
        }

        setBindViewHolder(binding, model, position, binding.root.context)
    }

    inner class BaseViewHolder(val binding: viewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            setViewHolder(binding, this)
        }
    }

    /*abstract class BaseDiffCallback<model>(private val oldList: List<model>, private val newList: List<model>) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].aa == newList[newItemPosition].aa
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            return oldList[oldPosition] == newList[newPosition]
        }

        @Nullable
        override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
            return super.getChangePayload(oldPosition, newPosition)
        }
    }*/

}

abstract class BaseDiffCallback<model : Any> : DiffUtil.ItemCallback<model>() {

    override fun areItemsTheSame(oldItem: model, newItem: model): Boolean {
        return oldItem == newItem
    }

}