package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.RecyclerViewAdapter
import com.graduate.datn.custom.view.TypeServiceView
import com.graduate.datn.extension.*
import kotlinx.android.synthetic.main.item_type_service.view.*

class TypeServiceAdapter(context: Context) : RecyclerViewAdapter(context, true) {

    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return BloodGroupViewHolder(parent.inflate(R.layout.item_type_service))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position,TypeServiceView.TypeServiceGroup::class.java)
        item?.let {
            (holder as BloodGroupViewHolder).view.apply {
                tv_type_service.text = it.title
            }

        }

        item?.let {
            (holder as BloodGroupViewHolder).view.apply {
                if(isItemSelected(position)){
//                    holder.itemView.tv_blood.setBackgroundResource(R.drawable.bg_service_selected)
                    holder.itemView.imv_checker.visible()
                }else{
//                    holder.itemView.tv_blood.setBackgroundResource(R.drawable.bg_select)
                    holder.itemView.imv_checker.gone()
                }
            }

        }
    }

    inner class BloodGroupViewHolder(val view: View) : NormalViewHolder(view){
        init {
            view.onAvoidDoubleClick {
                val item = getItem(adapterPosition, TypeServiceView.TypeServiceGroup::class.java)
                getListWrapperModels()?.forEachIndexed { index, modelWrapper ->
                    setSelectedItem(index,false)
                }
                setSelectedItem(adapterPosition,true)
                item?.let{
                    onClickItem(item)
                }
            }
        }
    }

    var onClickItem :(TypeServiceView.TypeServiceGroup) -> Unit = {}

}