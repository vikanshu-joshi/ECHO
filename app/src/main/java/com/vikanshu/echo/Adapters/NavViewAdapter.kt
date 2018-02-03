package com.vikanshu.echo.Adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.vikanshu.echo.Data.NavViewData

class NavViewAdapter(ctx: Context,List: ArrayList<NavViewData>): BaseAdapter() {

    var context = ctx
    var list = List

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView: View = convertView!!
        return itemView
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return list.count()
    }
}