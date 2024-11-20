package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.RecyclerViewAdapter
import com.graduate.datn.custom.view.CustomSelectTime
import com.graduate.datn.extension.inflate
import com.graduate.datn.extension.onAvoidDoubleClick
import kotlinx.android.synthetic.main.item_select_request.view.*

class CustomTimeAdapter(context: Context): RecyclerViewAdapter(context, true) {
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return RequestTypeViewHolder(parent.inflate(R.layout.item_select_request))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, CustomSelectTime.RequestType::class.java)
        item?.let {
            (holder as RequestTypeViewHolder).view.apply {
                tv_item_select.text = it.title

                if (isItemSelected(position)) {
                    holder.itemView.tv_item_select.setBackgroundResource(R.color.color_start_header_account)
                } else {
                    holder.itemView.tv_item_select.setBackgroundResource(R.color.md_white_1000)
                }
            }

        }
    }

    inner class RequestTypeViewHolder(val view: View) : NormalViewHolder(view) {
        init {
            view.onAvoidDoubleClick {
                val item = getItem(adapterPosition, CustomSelectTime.RequestType::class.java)
                getListWrapperModels()?.forEachIndexed { index, modelWrapper ->
                    setSelectedItem(index, false)
                }
                setSelectedItem(adapterPosition, true)
                item?.let {
                    onClickItem(item)
                }
            }
        }
    }

    var onClickItem: (CustomSelectTime.RequestType) -> Unit = {}
}