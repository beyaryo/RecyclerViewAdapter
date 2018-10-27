package com.lynx.wind.recyclersample.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import com.lynx.wind.recyclersample.model.User
import kotlinx.android.synthetic.main.item_user.view.*

class SimpleHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(data: User){
        itemView.txt.text = data.name
    }
}