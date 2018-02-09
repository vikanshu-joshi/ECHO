package com.vikanshu.echo.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.vikanshu.echo.Data.SongsData
import com.vikanshu.echo.R


class FavouritesAdapter(context: Context?, songsList: ArrayList<SongsData>): BaseAdapter() {

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
        var songName: TextView?= null
        var artistName: TextView?= null
    }
}