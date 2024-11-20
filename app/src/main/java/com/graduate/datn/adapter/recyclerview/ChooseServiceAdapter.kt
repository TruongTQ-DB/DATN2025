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
import kotlinx.android.synthetic.main.item_choose_service.view.*

class ChooseServiceAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (String, String) -> Unit = { id, _ ->}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_choose_department))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, ServiceItem::class.java)
        item?.let {
            holder.itemView.apply {
                tv_name.text = it.data.name
                image.loadImageUrl(it.data.image)

                this.onAvoidDoubleClick {
                    item.data.name?.let { it1 -> onClick(item.id, it1) }
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)

}