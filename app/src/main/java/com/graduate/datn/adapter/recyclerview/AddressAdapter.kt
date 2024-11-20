package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.response.ListAddressResponse
import com.graduate.datn.extension.*
import kotlinx.android.synthetic.main.item_address.view.*
import java.io.Serializable

class AddressAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (AddressItem) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_address))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, AddressItem::class.java)
        item?.let {
            holder.itemView.apply {
                tv_name.text = it.data?.name
                tv_content.text = it.data?.address
                imv_avatar.loadImageUrl(it.data?.avata)

                this.onAvoidDoubleClick {
                    onClick(item)
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)

}


data class AddressItem(val id: String?= null, val data: ListAddressResponse?= null): Serializable
