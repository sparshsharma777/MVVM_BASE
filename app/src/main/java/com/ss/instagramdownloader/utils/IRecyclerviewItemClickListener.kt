package com.ss.instagramdownloader.utils



interface IRecyclerviewItemClickListener {
    fun onItemClick(position: Int? = -1, model: Any, tag: Any? = null)
}