package com.mankirat.common.lib.base

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<model : Any, viewBinding : ViewBinding>(baseDiffUtil: DiffUtil.ItemCallback<model>, private val singleSelection: Boolean = false) :
    ListAdapter<model, BaseAdapter<model, viewBinding>.BaseViewHolder>(baseDiffUtil) {

    abstract fun bindView(binding: viewBinding, model: model, position: Int, context: Context)

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

        holder.itemView.setOnClickListener {
            clickListener?.invoke(model, position)
            if (singleSelection) selectedPos = position
        }

        bindView(binding, model, position, holder.itemView.context)
    }

    //override fun onViewRecycled(holder: BaseViewHolder) {
    //    holder.itemView.setOnClickListener(null)
    //    super.onViewRecycled(holder)
    //}

    inner class BaseViewHolder(val binding: viewBinding) : RecyclerView.ViewHolder(binding.root)

}