package com.mankirat.common.lib.base

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter2<model : Any, viewBinding : ViewBinding>(private val singleSelection: Boolean = false) :
    RecyclerView.Adapter<BaseAdapter2<model, viewBinding>.BaseViewHolder>() {

    abstract fun bindView(binding: viewBinding, model: model, position: Int, context: Context)

    abstract fun areContentsTheSame(oldItem: model, newItem: model): Boolean

    var clickListener: ((model: model, position: Int) -> Unit)? = null
    var selectedPos: Int = 0
        set(value) {
            val oldPos = field
            field = value
            notifyItemChanged(oldPos)
            notifyItemChanged(value)
        }

    var data = ArrayList<model>()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(BaseDiffCallback(field, value))
            //field = value
            field.clear()
            field.addAll(value)
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onBindViewHolder(holder: BaseAdapter2<model, viewBinding>.BaseViewHolder, position: Int) {
        val model = data[position]
        val binding = holder.binding

        holder.itemView.setOnClickListener {
            clickListener?.invoke(model, position)
            if (singleSelection) selectedPos = position
        }

        bindView(binding, model, position, holder.itemView.context)
    }

    override fun getItemCount() = data.size

    inner class BaseViewHolder(val binding: viewBinding) : RecyclerView.ViewHolder(binding.root)

    inner class BaseDiffCallback(private val oldList: List<model>, private val newList: List<model>) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
            return (oldList[oldPos] == newList[newPos])
        }

        override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
            return areContentsTheSame(oldList[oldPos], newList[newPos])
        }

    }

}