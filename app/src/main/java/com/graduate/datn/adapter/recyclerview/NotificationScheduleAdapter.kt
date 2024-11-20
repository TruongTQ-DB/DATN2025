package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.extension.inflate
import com.graduate.datn.extension.loadImageUrl
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.ui.admin.list_work_schedule.DateToWorkItem
import kotlinx.android.synthetic.main.item_notification_schedule.view.*

class NotificationScheduleAdapter(context: Context) :
    EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (DateToWorkItem) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_notification_schedule))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, DateToWorkItem::class.java)
        item?.let {
            holder.itemView.apply {
                image_view.loadImageUrl(it.data.avatar)
                tv_name.text = it.data.name
                tv_content.text = it.data.name + " đã gửi yêu cầu xét duyệt lịch làm việc!"

                this.onAvoidDoubleClick {
                    onClick(item)
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)
}