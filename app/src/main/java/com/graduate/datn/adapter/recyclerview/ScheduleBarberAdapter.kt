package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.extension.*
import kotlinx.android.synthetic.main.item_schedule.view.*

class ScheduleBarberAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (String) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_schedule))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, BookingItem::class.java)
        item?.let {
            holder.itemView.apply {
                barber_name.text = it.data.nameUser
                tv_barber_shop_address.text = it.data.phoneUser
                img_barber.loadImageUrl(it.data.avatarUser)
                tv_daytime.text = it.data.date?.formatDateSchedule() + " | " + it.data.timeFrom
                tv_optional_service_name.gone()

                tv_status.text = when (it.data.status) {
                    0 -> {
                        tv_status.background =
                            context.getDrawable(R.drawable.bg_schedule_status_new)
                        "Mới"
                    }
                    1 -> {
                        tv_status.background =
                            context.getDrawable(R.drawable.bg_schedule_status_done)
                        "Đã cắt"
                    }
                    2 -> {
                        tv_status.background =
                            context.getDrawable(R.drawable.bg_schedule_status_cancel)
                        "Đã huỷ"
                    }
                    else -> {
                        "Đã Huỷ"
                    }
                }

                this.onAvoidDoubleClick {
                    onClick(item.id)
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)

}