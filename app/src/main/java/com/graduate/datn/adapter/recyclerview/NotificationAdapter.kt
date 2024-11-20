package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.response.NotificationResponse
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.inflate
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.extension.visible
import kotlinx.android.synthetic.main.item_notification.view.*

class NotificationAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (NotificationItem) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_notification))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, NotificationItem::class.java)
        item?.let {
            holder.itemView.apply {
                tv_title.text = it.data.title
                tv_content.text = it.data.message
                if (it.data.status == 0) {
                    tv_status.visible()
                }else {
                    tv_status.gone()
                }

                this.onAvoidDoubleClick {
                    onClick(item)
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)
}

data class NotificationItem(var id: String, var data: NotificationResponse)