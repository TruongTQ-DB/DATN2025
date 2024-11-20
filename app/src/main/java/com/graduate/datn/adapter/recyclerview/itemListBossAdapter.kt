package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.RecyclerViewAdapter
import com.graduate.datn.entity.boss.ListBoss
import com.graduate.datn.extension.inflate
import com.graduate.datn.extension.onAvoidDoubleClick
import kotlinx.android.synthetic.main.item_manager.view.*
import kotlinx.android.synthetic.main.item_manager.view.imv_select
import kotlinx.android.synthetic.main.view_custom_request_type.view.*
import javax.inject.Inject

class itemListBossAdapter @Inject constructor(context: Context): RecyclerViewAdapter(context, false) {
    var posiItem = -1
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return managerViewHodel(parent.inflate(R.layout.item_manager))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, ListBoss::class.java)
        item?.let {
            holder.itemView.apply {
               tv_name.text = it!!.name
            }
        }
        item?.let {
            holder.itemView.apply {
                cv_container.onAvoidDoubleClick {
                    imv_select.setImageDrawable(context.getDrawable(R.drawable.avatar_circle))
                    onClick(item, position)
//                    if (posiItem != position){
//                        val item = getItem(posiItem, ListBoss::class.java)
//                        holder.itemView.imv_select.setImageDrawable(context.getDrawable(R.drawable.bg_avt))
//                        posiItem = position
//                    }
                }
            }
        }
    }

    inner class managerViewHodel(view: View): NormalViewHolder(view)

    var onClick: (ListBoss, position: Int) -> Unit = { it, position -> }
}