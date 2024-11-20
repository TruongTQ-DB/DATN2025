package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.User
import com.graduate.datn.extension.inflate
import com.graduate.datn.extension.loadImageUrl
import com.graduate.datn.extension.onAvoidDoubleClick
import com.graduate.datn.ui.admin.list_work_schedule.DateToWorkItem
import kotlinx.android.synthetic.main.item_docter_list_name.view.*
import kotlinx.android.synthetic.main.item_docter_list_namenew.view.btn_confirm
import kotlinx.android.synthetic.main.item_docter_list_namenew.view.tv_adress


class DocterListNameAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (Any) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_docter_list_namenew))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, Any::class.java)
        if (item is User) {
            item.let {
                holder.itemView.apply {
                    tv_name.text = it?.name
                    tv_detail_name.text = it?.detailname
                    tv_adress.text = it?.address
                    img_avatar.loadImageUrl(it?.avatar)

                    btn_confirm.onAvoidDoubleClick {
                        onClick(item)
                    }
                }
            }
        } else if (item is DateToWorkItem) {
            item.let {
                holder.itemView.apply {
                    tv_name.text = it.data.name
                    tv_detail_name.text = it.data.date
                    img_avatar.loadImageUrl(it.data.avatar)

                    btn_confirm.onAvoidDoubleClick {
                        onClick(item)
                    }
                }
            }
        }

    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)

}