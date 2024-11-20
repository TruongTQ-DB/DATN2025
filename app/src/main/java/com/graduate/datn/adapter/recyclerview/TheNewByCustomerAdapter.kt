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
import kotlinx.android.synthetic.main.item_the_new_by_customer.view.*

class TheNewByCustomerAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (TheNewItem) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_the_new_by_customer))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, TheNewItem::class.java)
        item.let {
            holder.itemView.apply {
                tv_title_the_new.text = it?.data?.title
                img_the_new.loadImageUrl(it?.data?.image)

                this.onAvoidDoubleClick {
                    item?.let { it1 -> onClick(it1) }
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)
}