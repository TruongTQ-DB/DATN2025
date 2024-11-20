package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.entity.response.OptionalServiceResponse
import com.graduate.datn.entity.response.ServiceResponse
import com.graduate.datn.extension.*
import kotlinx.android.synthetic.main.item_service.view.*
import java.io.Serializable

class ServiceAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    var onClick: (Any) -> Unit = {}
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        return HistoryExaminationViewHolder(parent.inflate(R.layout.item_service))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, Any::class.java)
        if (item is ServiceItem) {
            setUpView(true, holder.itemView)
            item.let {
                holder.itemView.apply {
                    tv_name.text = it.data.name
                    tv_service.text = it.data.addressName
                    img_service.loadImageUrl(it.data.image)

                    this.onAvoidDoubleClick {
                        onClick(item)
                    }
                }
            }
        } else if (item is OptionalServiceItem) {
            item.let {
                setUpView(true, holder.itemView)
                holder.itemView.apply {
                    tv_name.text = it.data.name
                    tv_service.text = it.data.serviceName
                    img_service.loadImageUrl(it.data.image)

                    this.onAvoidDoubleClick {
                        onClick(item)
                    }
                }
            }
        }
    }

    private fun setUpView(show: Boolean, view: View) {
        if(show){
            view.imv_line.visible()
            view.tv_service.visible()
        } else {
            view.imv_line.gone()
            view.tv_service.gone()
        }
    }

    inner class HistoryExaminationViewHolder(view: View) : NormalViewHolder(view)
}

data class ServiceItem(val id: String, val data: ServiceResponse) : Serializable

data class OptionalServiceItem(val id: String, val data: OptionalServiceResponse) : Serializable

