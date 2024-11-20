package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.extension.inflate
import com.graduate.datn.extension.onAvoidDoubleClick
import kotlinx.android.synthetic.main.item_choose_service_top.view.*

class ChooseServiceTopAdapter(context: Context) :
    EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (String) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_choose_service_top))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, ServiceItem::class.java)
        item?.let {
            holder.itemView.apply {
                name.text = it.data.name
                if (item.data.isSelected) {
                    cl_container.setBackgroundResource(R.drawable.bg_add_address)
                    name.setTextColor(context.getColor(R.color.white))
                } else {
                    cl_container.setBackgroundResource(R.drawable.bg_item_unselected)
                    name.setTextColor(context.getColor(R.color.md_black_1000))
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view) {
        init {
            itemView.onAvoidDoubleClick {
                val item = getItem(adapterPosition, ServiceItem::class.java)
                item?.let {
                    getListWrapperModels()?.mapIndexed { index, wrapperModel ->
                        (wrapperModel.model as ServiceItem).data.isSelected = false
                        notifyItemChanged(index, "$index")
                    }
                    it.data.isSelected = !it.data.isSelected
                    notifyItemChanged(adapterPosition, "$adapterPosition")
                    onClick(item.id)
                }
            }
        }
    }

    fun clearSelected() {
        getListWrapperModels()?.map {
            (it.model as ServiceItem)
        }?.forEach { it.data.isSelected = false }
        notifyDataSetChanged()
    }
}