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
import kotlinx.android.synthetic.main.item_choose_address.view.*

class ChooseAddressAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (AddressItem) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_choose_address))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, AddressItem::class.java)
        item?.let {
            holder.itemView.apply {
                barber_shop_name.text = it.data?.name
                tv_barber_shop_address.text = it.data?.address
                img_barber_shop.loadImageUrl(it.data?.avata)

                this.onAvoidDoubleClick {
                    onClick(item)
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)

}