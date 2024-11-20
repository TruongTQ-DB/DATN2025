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
import kotlinx.android.synthetic.main.item_chat.view.*

class ListChatAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (AddressItem) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_chat))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, AddressItem::class.java)
        item?.let {
            holder.itemView.apply {
                tv_name.text = it.data?.name
                tv_message.text = it.data?.address
                imv_avatar.loadImageUrl(it.data?.avata)

                this.onAvoidDoubleClick {
                    onClick(item)
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)

}
