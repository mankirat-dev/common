@file:Suppress("unused")

package com.mankirat.common.lib.base

import android.content.Context
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<model : Any, viewBinding : ViewBinding>(private val singleSelection: Boolean = false) :
    ListAdapter<model, BaseAdapter<model, viewBinding>.BaseViewHolder>(BaseDiffCallback<model>()) {

    abstract fun setBindViewHolder(binding: viewBinding, model: model, position: Int, context: Context)

    open fun setViewHolder(binding: viewBinding, viewHolder: BaseViewHolder) {}

    abstract fun areContentsTheSame(oldItem: model, newItem: model): Boolean

    //val diffCallback = BaseDiffCallback(field, value)

//    var data = listOf<model>()
//        set(value) {
//            val diffResult = DiffUtil.calculateDiff(diffCallback)
//            field = value
//            //notifyDataSetChanged()
//            diffResult.dispatchUpdatesTo(this)
//        }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val model = getItem(position)
        //val model = data[position]
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

    var clickListener: ((model: model, position: Int) -> Unit)? = null

    var selectedPos: Int = 0
        set(value) {
            val oldPos = field
            field = value
            notifyItemChanged(oldPos)
            notifyItemChanged(value)
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

    class BaseDiffCallback<model : Any> : DiffUtil.ItemCallback<model>() {

        override fun areItemsTheSame(oldItem: model, newItem: model): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: model, newItem: model): Boolean {
            return areContentsTheSame(oldItem, newItem)
        }
    }

}
