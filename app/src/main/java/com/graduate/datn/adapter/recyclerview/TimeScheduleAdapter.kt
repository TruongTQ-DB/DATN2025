package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.request.TimeRange
import com.graduate.datn.extension.inflate
import com.graduate.datn.extension.onAvoidDoubleClick
import kotlinx.android.synthetic.main.item_work_schedule.view.*
import kotlinx.android.synthetic.main.layout_item_time_schedule.view.*
import java.text.MessageFormat

class TimeScheduleAdapter(context: Context) :
    EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClickItem:(TimeRange, Int) -> Unit = {_, _ ->}

    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return TimeScheduleViewHolder(parent.inflate(R.layout.layout_item_time_schedule))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, TimeRange::class.java)
        item?.let {
            (holder as TimeScheduleViewHolder).itemView.apply {
                tv_time.text = MessageFormat.format(context.getString(R.string.str_time_schedule), item.startTime, item.endTime)
                if (item.selector == true) {
                    tv_time.setBackgroundResource(R.drawable.bg_time_schedule_selected)
                    tv_time.setTextColor(context.getColor(R.color.white))
                } else {
                    tv_time.setBackgroundResource(R.drawable.bg_time_schedule_normal)
                    tv_time.setTextColor(context.getColor(R.color.md_black_1000))
                }
            }
        }
    }

    inner class TimeScheduleViewHolder(itemView: View) : NormalViewHolder(itemView) {
        init {
            itemView.onAvoidDoubleClick {
                val item = getItem(position, TimeRange::class.java)
                item?.let {
                    getListWrapperModels()?.mapIndexed{ index, wrapperModel ->
                        (wrapperModel.model as TimeRange).selector = false
                        notifyItemChanged(index, "$index")
                    }
                    it.selector = !it.selector!!
                    notifyItemChanged(adapterPosition, "$adapterPosition")
                    onClickItem(item, position)
                }
            }
        }
    }
}