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
import kotlinx.android.synthetic.main.item_booking_customer.view.*


class BookingCustomerAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (String) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_booking_customer))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, BookingItem::class.java)
        item?.let {
            holder.itemView.apply {
                tv_name.text = it.data.nameUser
                tv_gender.text = it.data.phoneUser
                imv_avatar.loadImageUrl(it.data.avatarUser)

                this.onAvoidDoubleClick {
                    item.data.userId?.let { it1 -> onClick(it1) }
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)

}