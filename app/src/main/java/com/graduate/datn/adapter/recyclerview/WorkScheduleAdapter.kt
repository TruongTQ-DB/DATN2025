package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.extension.*
import com.graduate.datn.ui.admin.list_work_schedule.DateToWorkItem
import kotlinx.android.synthetic.main.item_work_schedule.view.*

class WorkScheduleAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (String) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_work_schedule))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, DateToWorkItem::class.java)
        item?.let {
            holder.itemView.apply {
                name.text = it.data.name
                content.text = "${it.data.date}"
                image.loadImageUrl(it.data.avatar)

                this.onAvoidDoubleClick {
                    onClick(item.id)
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)

}