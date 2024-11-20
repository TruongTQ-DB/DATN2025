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
import kotlinx.android.synthetic.main.item_optional_service.view.*

class OptionalServiceAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {
    var onClick: (OptionalServiceItem) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_optional_service))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, OptionalServiceItem::class.java)
        item?.let {
            holder.itemView.apply {
                tv_name.text = it.data.name
                tv_content.text = it.data.price+ "VND"
                img_optional_service.loadImageUrl(it.data.image)

                this.onAvoidDoubleClick {
                    onClick(item)
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)
}
