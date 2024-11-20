package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.calendar.Data
import com.graduate.datn.extension.convertDate
import com.graduate.datn.extension.convertTime
import com.graduate.datn.extension.inflate
import kotlinx.android.synthetic.main.item_timekeeping.view.*
import javax.inject.Inject

class TimeKeepingAdapter @Inject constructor(context: Context): EndlessLoadingRecyclerViewAdapter(context, false) {
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return TimekeepingViewHoder(parent.inflate(R.layout.item_timekeeping))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, Data::class.java)
        item?.let {
            holder.itemView.apply {
                tv_name.text = it.name
                tv_day.text = it.dateTime.convertDate()
                tv_time.text = it.dateTime.convertTime()
            }
        }
    }

    inner class TimekeepingViewHoder(view: View): NormalViewHolder(view)
}