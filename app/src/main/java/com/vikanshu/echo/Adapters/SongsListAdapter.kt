package com.vikanshu.echo.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.vikanshu.echo.Data.SongsData
import com.vikanshu.echo.R

class SongsListAdapter(context: Context?,songsList: ArrayList<SongsData>): BaseAdapter() {

    var ctx = context
    var songs = songsList

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView : View
        val holder : Holder
        if (convertView == null){
            itemView = LayoutInflater.from(ctx).inflate(R.layout.song_view,null)
            holder = Holder()
            holder.songName = itemView.findViewById(R.id.songName)
            holder.artistName = itemView.findViewById(R.id.artistName)
            itemView.tag = holder
        }else{
            itemView = convertView
            holder = itemView.tag as Holder
        }
        holder.songName?.text = songs[position].title
        if (songs[position].artist == "<unknown>")
            holder.artistName?.text = "unknown artist"
        else
            holder.artistName?.text = songs[position].artist
        return itemView
    }

    override fun getItem(position: Int): Any {
        return songs[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return songs.count()
    }

    private class Holder{
        var songName: TextView ?= null
        var artistName: TextView ?= null
    }

    /* override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(ctx).inflate(R.layout.song_view,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        holder?.songName?.text = songs[position].title
        holder?.artistName?.text = songs[position].artist
    }

    override fun getItemCount(): Int {
        return songs.count()
    }

    class MyViewHolder(v: View): RecyclerView.ViewHolder(v) {
        var songName = v.findViewById<TextView>(R.id.songName)
        var artistName = v.findViewById<TextView>(R.id.artistName)
    } */
}
