package com.graduate.datn.adapter.recyclerview

import android.content.Context
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter

class ChatAdapter(context: Context, val fragmentManager: FragmentManager): EndlessLoadingRecyclerViewAdapter(context, false) {

    companion object {
        const val VIEW_TYPE_MY_TEXT_MESSAGE = -1

        const val VIEW_TYPE_OPPOSITE_TEXT_MESSAGE = -2
    }
    override fun initNormalViewHolder(parent: ViewGroup): RecyclerView.ViewHolder? {
        TODO("Not yet implemented")
    }

    override fun bindNormalViewHolder(holder: NormalViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}