package com.graduate.datn.extension

import androidx.recyclerview.widget.RecyclerView
import com.graduate.datn.base.adapter.BaseRecyclerView
import com.graduate.datn.base.adapter.EndlessLoadingRecyclerViewAdapter
import com.graduate.datn.base.adapter.RecyclerViewAdapter

infix fun BaseRecyclerView.onRefresh(init: () -> Unit) {
    setOnRefreshListener { init() }
}

infix fun BaseRecyclerView.onLoadingMore(init: () -> Unit) {
    setOnLoadingMoreListener(object : EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
        override fun onLoadMore() {
            init()
        }
    })
}


infix fun EndlessLoadingRecyclerViewAdapter.onLoadingMore(init: () -> Unit) {
    setLoadingMoreListener(object : EndlessLoadingRecyclerViewAdapter.OnLoadingMoreListener {
        override fun onLoadMore() {
            init()
        }
    })
}

infix fun BaseRecyclerView.onItemClick(onClick: (Int) -> Unit) {
    setOnItemClickListener(object : RecyclerViewAdapter.OnItemClickListener{
        override fun onItemClick(
            adapter: RecyclerView.Adapter<*>,
            viewHolder: RecyclerView.ViewHolder?,
            viewType: Int,
            position: Int
        ) {
            onClick(position)
        }
    })
}

infix fun RecyclerViewAdapter.onItemClick(onClick: (Int) -> Unit) {
    addOnItemClickListener(object : RecyclerViewAdapter.OnItemClickListener{
        override fun onItemClick(
            adapter: RecyclerView.Adapter<*>,
            viewHolder: RecyclerView.ViewHolder?,
            viewType: Int,
            position: Int
        ) {
            onClick(position)
        }
    })
}