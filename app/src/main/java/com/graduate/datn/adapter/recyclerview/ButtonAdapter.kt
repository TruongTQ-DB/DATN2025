package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.R
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.extension.inflate
import kotlinx.android.synthetic.main.item_button_home.view.*

class ButtonAdapter(context: Context) : EndlessLoadingRecyclerViewAdapter(context, false) {

    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ButtonViewHolder(parent.inflate(R.layout.item_button_home))
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        val item = getItem(position, Buttons::class.java)
        item?.let {
            (holder as ButtonViewHolder).view.apply {
                image.setImageResource(it.serviceIcon)
                tv_button.text = context.getString(it.serviceTitle)
            }

        }
    }

    inner class ButtonViewHolder(val view: View) : NormalViewHolder(view)
}

enum class Buttons(
    @StringRes val serviceTitle : Int,
    @DrawableRes val serviceIcon : Int
) {
    SPECIALIST(R.string.str_appointment_schedule, R.drawable.img_datlichkham),
    COUNSELING(R.string.str_tu_van,R.drawable.img_video),
    GENERA(R.string.str_tiem_chung,R.drawable.img_tiemchung),
    TEST(R.string.str_goi_kham,R.drawable.img_goikham),
    CANCER(R.string.str_xet_nghiem,R.drawable.img_xetnghiem),
    AT_HOME(R.string.str_kham_tai_nha,R.drawable.img_ytetainha)
}