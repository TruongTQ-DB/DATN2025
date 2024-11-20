package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.OptionalService
import com.graduate.datn.extension.gone
import com.graduate.datn.extension.inflate
import com.graduate.datn.extension.onAvoidDoubleClick
import kotlinx.android.synthetic.main.item_select_optional_service.view.*

class SelectOptionalServiceAdapter(context: Context) :
    EndlessLoadingRecyclerViewAdapter(context, false) {
    var onDeleteOptionalService: (OptionalService, Int) -> Unit = { _, _ -> }
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_select_optional_service))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, OptionalService::class.java)
        item?.let {
            holder.itemView.apply {
                tv_name.text = it.optionalServiceName
                if (it.disable == true){
                    imv_delete.gone()
                }
            }
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view) {
        init {
            view.imv_delete.onAvoidDoubleClick {
                val item = getItem(bindingAdapterPosition, OptionalService::class.java)
                if (item != null) {
                    onDeleteOptionalService(item, bindingAdapterPosition)
                }
            }
        }
    }
}

