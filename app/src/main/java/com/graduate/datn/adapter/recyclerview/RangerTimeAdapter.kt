package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.request.TimeRange
import com.graduate.datn.extension.*
import kotlinx.android.synthetic.main.item_ranger_time.view.*

class RangerTimeAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_ranger_time))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, TimeRange::class.java)
        item?.let {
            holder.itemView.apply {
                tv_ranger_time.text = it.startTime + " - " + it.endTime
                tv_status.text = when (it.stater) {
                    0 -> {
                        tv_status.background =
                            context.getDrawable(R.drawable.bg_schedule_status_new)
                        "Mới"
                    }
                    1 -> {
                        tv_status.background =
                            context.getDrawable(R.drawable.bg_schedule_status_done)
                        "Đã đặt"
                    }
                    else -> {""}
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)

}
