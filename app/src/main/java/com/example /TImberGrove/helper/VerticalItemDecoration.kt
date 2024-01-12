package com.example.timbertrove.helper

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

// this is for adding spaces in between recycler items
class VerticalItemDecoration(private val amount : Int = 30) : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.bottom = amount
    }
}
