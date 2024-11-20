package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.extension.inflate
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.ui.admin.list_work_schedule.DateToWorkItem
import kotlinx.android.synthetic.main.item_list_date_schedule.view.*

class ListDateScheduleAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onApprove: (DateToWorkItem, Int) -> Unit = {_, _ ->}
    var onClick:(DateToWorkItem) -> Unit = {}

    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_list_date_schedule))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, DateToWorkItem::class.java)
        item?.let {
            holder.itemView.apply {
                tv_title.text = it.data.date
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view) {
        init {
            view.tv_approve.onAvoidDoubleClick {
                val item = getItem(position, DateToWorkItem::class.java)
                item?.let { it1 -> onApprove(it1, bindingAdapterPosition) }
            }

            view.onAvoidDoubleClick {
                val item = getItem(position, DateToWorkItem::class.java)
                item?.let { it1 -> onClick(it1) }
            }
        }
    }
}