@file:Suppress("unused")

package com.mankirat.common.lib.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseAdapter<T, viewBinding : ViewBinding>(val singleSelection: Boolean = false) : RecyclerView.Adapter<BaseAdapter<T, viewBinding>.BaseViewHolder>() {

    var list: ArrayList<T> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    open fun setData(list: ArrayList<T>) {
        this.list = list
        notifyDataSetChanged()
    }


    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val model = list[position]
        val binding = holder.binding

        setBindViewHolder(binding, model, position, binding.root.context)
    }

    override fun getItemCount() = list.size


    abstract fun setBindViewHolder(binding: viewBinding, model: T, position: Int, context: Context)

    open fun setViewHolder(binding: viewBinding, viewHolder: BaseViewHolder) {}


    inner class BaseViewHolder(val binding: viewBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            setViewHolder(binding, this)

            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position == RecyclerView.NO_POSITION) return@setOnClickListener

                clickListener?.invoke(binding, list[position], position)

                if (singleSelection) {
                    selectedPos = position
                }
            }

        }

    }

    var clickListener: ((view: viewBinding, model: T, position: Int) -> Unit)? = null

    var selectedPos: Int = 0
        set(value) {
            val oldPos = field
            field = value
            notifyItemChanged(oldPos)
            notifyItemChanged(field)
        }

}