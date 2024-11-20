package com.graduate.datn.ui.user.container_slider

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.adapter.recyclerview.ServiceItem
import com.graduate.datn.adapter.recyclerview.TheNewItem
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.extension.inflate
import com.graduate.datn.extension.loadImageUrl
import com.graduate.datn.extension.onAvoidDoubleClick
import kotlinx.android.synthetic.main.item_choose_service.view.image
import kotlinx.android.synthetic.main.item_choose_service.view.tv_name
import kotlinx.android.synthetic.main.item_container_slider_img.view.slider_img
import kotlinx.android.synthetic.main.item_the_new_by_customer.view.img_the_new
import kotlinx.android.synthetic.main.item_the_new_by_customer.view.tv_title_the_new

class ContainSliderAdapter(contex: Context): EndlessLoadingRecyclerViewAdapter(contex,false) {
    var onClick: (TheNewItem) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_container_slider_img))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, TheNewItem::class.java)
        item.let {
            holder.itemView.apply {

                slider_img.loadImageUrl(it?.data?.image)

                this.onAvoidDoubleClick {
                    item?.let { it1 -> onClick(it1) }
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)

}